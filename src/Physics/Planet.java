package Physics;

import Elements.Bubble;
import Elements.Monster;
import Elements.SatelliteDish;
import Main.MainGame;
import Main.Res;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Planet {
    private class Grass{
        public static final int size = 60;
        public PVector pos;
        public PImage img;
        public float heading;

        public void display(){
            p.pushMatrix();
            p.translate(pos.x, pos.y);
            p.rotate(heading);
            p.image(img, 0, 0);
            p.popMatrix();
        }
    }

    protected static final float BUBBLE_FACTOR = 0.015f;

    protected MainGame p;
    protected float radius;
    protected PVector position;

    protected PImage[] grassImgs;

    protected List<Grass> grass = null;
    protected float lowerAngle;
    protected float upperAngle;
    protected PVector lastLower;
    protected PVector lastUpper;
    protected float stepSize;

    protected boolean hasGetted = false;
    protected PImage getImg;
    private boolean gettingInProcess = false;

    public Planet(MainGame pa, float radius, PVector position) {
        p = pa;
        this.radius = radius;
        this.position = position;

        stepSize = 10f/radius;

        grassImgs = Res.GRASS;
    }

    public void display(boolean allowGet){
        if(allowGet && !hasGetted && !stillGrowing() && MainGame.requestGetterMutex()){
            // show whole planet in center if we are going to get this planet at this turn
            p.getUniverse().focusCameraOn(getPosition());
            gettingInProcess = true;
        }
        if(hasGetted){
            // just display the screenshot
            p.imageMode(p.CORNER);
            p.image(getImg, Math.round(position.x - radius - Grass.size), Math.round(position.y - radius - Grass.size),
                    Math.round(radius*2 +  2 * Grass.size), Math.round(radius*2 + 2 * Grass.size));
            p.imageMode(p.CENTER);
        }else{
            // build planet normally
            p.stroke(255);
            p.strokeWeight(3);
            p.fill(47, 22, 11);
            p.ellipseMode(p.RADIUS);
            p.ellipse(position.x, position.y, radius, radius);

            // display grass
            if(grass != null){
                for(Grass g: grass){
                    g.display();
                }

                if(stillGrowing()) {
                    contGrow();
                }else{
                    if(!hasGetted && allowGet && gettingInProcess){
                        // get screenshot NOW
                        p.imageMode(p.CORNER); // apparently this changes nuffin

                        PVector topLeft = new PVector(MainGame.WINDOW_WIDTH/2 - (radius + Grass.size),
                                MainGame.WINDOW_HEIGHT/2 - (radius + Grass.size));

                        p.loadPixels();
                        getImg = p.get(Math.round(topLeft.x), Math.round(topLeft.y), Math.round(radius*2 + 2*Grass.size), Math.round(radius*2 + 2*Grass.size));
                        hasGetted = true;
                        p.imageMode(p.CENTER);

                        // refocus on player
                        p.getUniverse().unfocusCamera();
                    }
                }
            }
        }


        if(hasGrass() && !stillGrowing()){

            if(p.random(0, 1) < BUBBLE_FACTOR){
                PVector pos = new PVector(0f, -radius);
                pos.rotate(p.random(0f, p.radians(360)));
                pos.add(position);
                Bubble b = new Bubble(p, pos, new PVector(0, 0), 0.001f);
                p.getUniverse().addBubble(b);
            }
        }
    }

    public List<Monster> addEnemies(int number){
        List<Monster> resL = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            float a = p.random(0, p.radians(360));
            PVector pos =  new PVector(0f, -(radius + Grass.size/2 + 0.2f));
            pos.rotate(a);
            pos = position.copy().add(pos);
            Monster m = new Monster(p, pos, new PVector(0, 0), 1f/100f);
            resL.add(m);
        }
        return resL;
    }

    public SatelliteDish addSatelliteDish(){
        float a = p.random(0, p.radians(360));
        PVector pos =  new PVector(0f, -(radius + Grass.size/2 + 0.2f));
        pos.rotate(a);
        pos = position.copy().add(pos);
        SatelliteDish d = new SatelliteDish(p, pos, new PVector(0, 0), 1f);
        return d;
    }

    public void contGrow(){
        float newLower = lowerAngle - p.radians(5);
        float newUpper = upperAngle + p.radians(5);

        for (float a = upperAngle; a < newUpper; a += stepSize) {
            Grass g = new Grass();
            g.pos = new PVector(0f, -(radius + Grass.size/2 + 0.2f));
            g.pos.rotate(a);
            g.pos.add(position);
            g.heading = a;
            g.img = grassImgs[(int) Math.floor(p.random(0, grassImgs.length))];
            grass.add(g);
            lastUpper = g.pos;
        }

        for (float a = lowerAngle; a > newLower; a -= stepSize) {
            Grass g = new Grass();
            g.pos = new PVector(0f, -(radius + Grass.size/2 + 0.2f));
            g.pos.rotate(a);
            g.pos.add(position);
            g.heading = a;
            g.img = grassImgs[(int) Math.floor(p.random(0, grassImgs.length))];
            grass.add(g);
            lastLower = g.pos;
        }

        lowerAngle = newLower;
        upperAngle = newUpper;
    }

    public void grow(float startAngle){
        lowerAngle = upperAngle = startAngle;
        grass = new ArrayList<>();
    }

    protected boolean stillGrowing(){
        if(lastUpper != null && lastLower != null){
            return grass.size() < 10 || lastUpper.dist(lastLower) > (7.5f/360f)*(2*Math.PI*radius);
        }
        return true;
    }

    public boolean hasGrass(){
        return grass!= null && grass.size() > 0;
    }

    public boolean readyToGet(){
        return !stillGrowing() && !hasGetted;
    }

    public void unGet(){
        hasGetted = false;
    }

    public float getRadius() {
        return radius;
    }

    public PVector getPosition() {
        return position;
    }
}
