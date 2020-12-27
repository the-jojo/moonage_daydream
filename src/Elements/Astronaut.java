package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class Astronaut extends PlayableGObject {
    public static final int LIFE_MAX = 3;
    public static final int AIR_MAX = 100;
    public static final int AIR_COUNT_MAX = 120;
    public static final int JET_COUNT_MAX = 100;
    public static final int BLU_COUNT_MAX = 5;

    protected boolean jetpacking = false;
    protected boolean jumping = false;
    protected boolean upReset = true;

    protected JetFire fireLeft;
    protected JetFire fireRight;
    protected JetFire fireCenter;

    protected int lifes = LIFE_MAX;
    protected int air = AIR_MAX;

    protected int airCounter = AIR_COUNT_MAX;
    protected int jetCounter = JET_COUNT_MAX;
    protected int bluCounter = 0;

    public Astronaut(MainGame pa) {
        super(pa);
    }

    @Override
    protected void setup() {
        height = 70;
        width = 38;
        pic = Res.ASTRONAUT;
        floatFactor = 0.75f;
        fireLeft = new JetFire(p);
        fireRight = new JetFire(p);
        fireCenter = new JetFire(p);
    }

    public Astronaut(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    @Override
    public void display() {
        // reduce air every 120 frames
        airCounter--;
        if(airCounter <= 0){
            air--;
            airCounter = AIR_COUNT_MAX;
        }

        // check if we ran out of air
        if(air <= 0){
            lifes--;
            if(lifes > 0)
                air = AIR_MAX;
            p.getUniverse().setMessage("You ran out of air and lost a life!");
        }

        // check if we hit a monster
        // (monsters can only hit us once every x frames -- this means we wont be hit again immediately in the
        // next frame if we still overlap with the monster)
        for(int i = 0; i < p.getUniverse().getMonsters().size(); i++){
            Monster monster = p.getUniverse().getMonsters().get(i);
            if(monster.exists() && monster.getPosition().dist(position) - monster.getWidth()/2 - height/2 <= 0 && monster.canHitPlayer()){
                if(isBelow(monster)){
                    // we jumped on the monster and killed it
                    monster.destroy();
                    // bounce
                    PVector up = new PVector(0f, -25);
                    up.rotate(heading);
                    velocity.add(up);
                }else{
                    // the monster hit us
                    // loose some air
                    for(float a = p.radians(-90); a <= p.radians(90); a += p.radians(30)){
                        PVector x = new PVector(0f, -100);
                        x.rotate(a);
                        x.rotate(heading);
                        PVector y = position.copy().add(x);
                        Bubble bubble = new Bubble(p, y, new PVector(0, 0), 0.001f);
                        p.getUniverse().addBubble(bubble);
                        air--;
                    }

                    // push the monster back
                    monster.pushBackFromPlayer(this);
                }
            }
        }

        // check if we hit a bubble
        for(int i = 0; i < p.getUniverse().getBubbles().size(); i++){
            Bubble b = p.getUniverse().getBubbles().get(i);
            if(b.exists() && b.getPosition().dist(position) - b.getWidth()/2 - height/2 <= 0){
                air++;
                b.destroy();
                bluCounter = BLU_COUNT_MAX; // paint the player blue for x number of frames
            }
        }

        // check if we hit a star
        for(int i = 0; i < p.getUniverse().getStars().size(); i++){
            Star b = p.getUniverse().getStars().get(i);
            if(b.exists() && b.getPosition().dist(position) - b.getWidth()/2 - height/2 <= 0){
                p.getUniverse().getCurPlanetSystem().addStarCollected();
                b.destroy();
            }
        }

        // user inputs
        if(leftPressed && !onPlanet){
            // move left in space --> right jet fire
            PVector down = new PVector(0f, height/2 - 10);
            PVector left = new PVector(width/2 + 10, 0f);
            down.rotate(heading);
            left.rotate(heading);
            PVector pos = position.copy().add(down).add(left);
            fireLeft.display(pos, heading - p.radians(180 + 30));
        }
        if(rightPressed && !onPlanet){
            // move right in space --> left jet fire
            PVector down = new PVector(0f, height/2 - 10);
            PVector right = new PVector(-width/2 - 5, 0f);
            down.rotate(heading);
            right.rotate(heading);
            PVector pos = position.copy().add(down).add(right);
            fireRight.display(pos, heading - p.radians(180 - 30));
        }
        if(jetpacking && !onPlanet && jetCounter > 0){
            // move up in space --> lower jet fire
            PVector down = new PVector(0f, height/2 + 10);
            down.rotate(heading);
            PVector pos = position.copy().add(down);
            fireCenter.display(pos, heading - p.radians(180));
            jetCounter--;
        }

        // paint player blue for some frames if he picked up some air
        if(bluCounter > 0){
            p.tint(0, 153, 204);  // Tint blue
            bluCounter--;
        }

        // display player
        super.display();

        // untint
        p.tint( 255, 255 );

        // move
        if (leftPressed) {
            left();
        }
        if (rightPressed) {
            right();
        }
        if (jetpacking && !onPlanet && jetCounter > 0) {
            jetUp(1.2f);
        }

    }

    /**
     * Launches the grass grow event from the player's heading all the way around the nearest planet
     */
    public void launchGrow(){
        nearestPlanet.grow(heading);
    }

    @Override
    public void setUpPressed(boolean upPressed) {
        if (upPressed) {
            if (upReset) {
                jump(15f);
                upReset = false;
            }
        } else {
            upReset = true;
            jetpacking = false;
            jumping = false;
        }
        super.setUpPressed(upPressed);
    }

    protected void jump(Float dist) {
        if (onPlanet) {
            jumping = true;
            jetpacking = false;
            PVector up = new PVector(0f, -dist);
            up.rotate(heading);
            velocity.add(up);
        } else {
            jetpacking = true;
            jumping = false;
        }
    }

    protected void jetUp(float dist) {
        PVector d = new PVector(0f, -dist);
        d.rotate(heading);
        velocity.add(d);
    }

    protected void left() {
        float dist = getDistForSideways();

        PVector d = new PVector(-dist, 0f);
        d.rotate(heading);
        position.add(d);

        if(onPlanet){
            PVector correcter = new PVector(0f, getMiddleOfLowerEdge().dist(nearestPlanet.getPosition()) - nearestPlanet.getRadius());
            correcter.rotate(heading);
            position.add(correcter);
            integrate();
        }

    }

    protected void right() {
        float dist = getDistForSideways();

        PVector d = new PVector(dist, 0f);
        d.rotate(heading);
        position.add(d);

        if(onPlanet){
            PVector correcter = new PVector(0f, getMiddleOfLowerEdge().dist(nearestPlanet.getPosition()) - nearestPlanet.getRadius());
            correcter.rotate(heading);
            position.add(correcter);
            integrate();
        }
    }

    /**
     * Returns the distance for sideways movement (left or right).
     * on the planet its always 20f.
     * In Space it scales with the distance from the planet surface but is max 30f.
     * @return
     */
    protected float getDistForSideways() {
        if(onPlanet)
            return 20f;
        float dist = 10f;
        float distToNearestSurface = nearestPlanet.getPosition().dist(position) - nearestPlanet.getRadius();
        dist += (distToNearestSurface * 0.005f);
        dist = Math.min(dist, 30f);
        return dist;
    }

    @Override
    public void integrate(Planet nearestPlanet, PVector gravity) {
        // velocity allowed only up and down (in relation to neatest planet)
        PVector relPosToPlanet = new PVector(position.x - nearestPlanet.getPosition().x, position.y - nearestPlanet.getPosition().y);
        float h = (p.radians(90) + relPosToPlanet.heading());
        velocity.rotate(-h);
        velocity.x = velocity.x* 0.95f;
        velocity.rotate(h);

        super.integrate(nearestPlanet, gravity);

        // point upwards from nearest planet
        turnTowards(h);

        // stick to surface of planet
        if((upPressed && touchesPlanet(getMiddleOfLowerEdge(), nearestPlanet)) ||
                (!upPressed && almostTouchesPlanet(getMiddleOfLowerEdge(), nearestPlanet))){
            PVector relPos = new PVector(
                    getPosition().x - nearestPlanet.getPosition().x,
                    getPosition().y - nearestPlanet.getPosition().y);
            float newX = (float)
                    (nearestPlanet.getPosition().x +
                            nearestPlanet.getRadius() * Math.sin(p.radians(90) + (relPos.heading())));
            float newY = (float)
                    (nearestPlanet.getPosition().y -
                            nearestPlanet.getRadius() * Math.cos(p.radians(90) + (relPos.heading())));
            PVector newPos = new PVector(newX, newY);

            PVector translateVector = getPosition().copy().sub(getMiddleOfLowerEdge());
            newPos.add(translateVector);

            position.x = newPos.x;
            position.y = newPos.y;
            velocity.x = 0;
            velocity.y = 0;
            onPlanet = true;
            jetCounter = JET_COUNT_MAX;
        }
    }

    public int getLifes() {
        return lifes;
    }

    public int getAir() {
        return air;
    }

    /**
     * Returns true if Monster is below astronautus
     * @return
     */
    public boolean isBelow(Monster monster){
        return monster.getMiddleOfLowerEdge().dist(monster.getNearestPlanet().getPosition()) + monster.getHeight()/2 <
                getMiddleOfLowerEdge().dist(nearestPlanet.getPosition());

    }
}
