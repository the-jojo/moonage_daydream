package Elements;

import Main.MainGame;
import Physics.Planet;
import processing.core.PImage;
import processing.core.PVector;

public abstract class GObject {
    protected MainGame p;

    // display info
    protected PImage pic;
    protected int height, width;

    // physics info
    protected float heading;
    protected PVector position;
    protected PVector velocity;
    protected float invMass;
    protected float dampen = .99f;
    protected boolean onPlanet = false;
    protected float floatFactor = 1;
    protected Planet nearestPlanet;
    protected PVector gravity;
    // Vector to accumulate forces prior to integration
    protected PVector forceAccumulator ;

    public GObject(MainGame p, PVector position, PVector velocity, float invMass) {
        this.p = p;
        this.position = position;
        this.velocity = velocity;
        this.invMass = invMass;
        forceAccumulator = new PVector(0f, 0f);
        setup();
    }

    public GObject(MainGame pa){
        p = pa;
        this.position = new PVector(0, 0);
        this.velocity = new PVector(0, 0);
        this.invMass = 0;
        forceAccumulator = new PVector(0f, 0f);
        setup();
    }

    protected abstract void setup();

    public void display(){
        p.pushMatrix();
        p.translate(position.x , position.y );
        p.rotate(heading);
        p.image(pic, 0, 0, width, height);
        p.popMatrix();
    }

    public void integrate(){
        integrate(nearestPlanet, gravity);
    }

    // update position and velocity with respect to gravity
    public void integrate(Planet nearestPlanet, PVector gravity) {
        this.nearestPlanet = nearestPlanet;
        this.gravity = gravity;

        // If infinite mass, we don't integrate
        if (invMass <= 0f) return ;

        // reset onPlanet
        onPlanet = false;

        if(nearestPlanet != null && gravity != null){
            // dampen gravity
            gravity.mult(floatFactor);
        }


        // update position
        position.add(velocity) ;

        // NB If you have a constant acceleration (e.g. gravity) start with
        //    that then add the accumulated force / mass to that.
        PVector resultingAcceleration = forceAccumulator.get() ;
        resultingAcceleration.mult(invMass) ;

        // update velocity
        if(nearestPlanet != null && gravity != null){
            velocity.add(gravity);
        }
        velocity.add(resultingAcceleration) ;
        // apply damping - disabled when Drag force present
        velocity.mult(dampen) ;


        // Clear accumulator
        forceAccumulator.x = 0 ;
        forceAccumulator.y = 0 ;
    }

    /**
     * Rotate towards part of the way towards a newHeading
     * @param newHeading
     */
    protected void turnTowards(float newHeading){
        if(onPlanet){
            heading = newHeading;
            return;
        }

        PVector old = new PVector(0f, -1);
        PVector nwe = new PVector(0f, -1);
        old.rotate(heading);
        nwe.rotate(newHeading);
        float dotProduct = old.dot(nwe);
        float angle = (float) Math.acos(dotProduct);

        if (Float.isNaN(angle)) {
            //System.out.println("Angle is NAN");
            return;
        }
        PVector nweNorm = new PVector(-nwe.y,nwe.x);
        float dotProduct2 = old.dot(nweNorm);
        if(dotProduct2>0) { //+ive value means the angle is <90deg therefore, ship_dir is on 'left'side of mouse dir, so clockwise is shortest route
            // rotate clockwise;
            heading = heading - angle * 0.1f;
        } else {
            // rotate anti_clockwise;
            heading = heading + angle * 0.1f;
        }
        return;
    }

    public PVector getMiddleOfLowerEdge(){
        PVector res = position.copy();
        PVector trs = new PVector(0f, height/2);
        trs.rotate(heading);
        res.add(trs);
        return res;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getCentrePos() {
        return position;//new PVector(position.x + width/2, position.y + height/2);
    }

    public float getHeading() {
        return heading;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    public boolean isOnPlanet() {
        return onPlanet;
    }

    protected boolean touchesPlanet(PVector position, Planet planet){
        return position.dist(planet.getPosition()) <= planet.getRadius();
    }

    protected boolean almostTouchesPlanet(PVector position, Planet planet){
        return position.dist(planet.getPosition()) <= planet.getRadius() + 2f;

    }

    public Planet getNearestPlanet(){
        return nearestPlanet;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }
}
