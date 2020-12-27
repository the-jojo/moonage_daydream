package Physics;

import Elements.GObject;
import processing.core.PVector;

import java.util.List;

public class Gravity extends ForceGenerator {
    protected List<Planet> planets;
    protected GObject obj;
    protected Planet nearestPlanet;
    protected PVector currentGravityForce;
    protected PVector previousGravityForce;

    public Gravity(List<Planet> planets, GObject obj) {
        this.planets = planets;
        this.obj = obj;
    }

    public void updateForceAndPlanet(){
        // find nearest planet to object
        Planet newNearest = null;
        float nearestDist = Float.MAX_VALUE;
        for(Planet planet : planets){
            if((planet.getPosition().dist(obj.getPosition()) - planet.getRadius()) <= nearestDist){
                newNearest = planet;
                nearestDist = planet.getPosition().dist(obj.getPosition()) - planet.getRadius();
            }
        }
        // if we switched planets, retain some of the gravity of the other planet
        if(newNearest != nearestPlanet){
            previousGravityForce = currentGravityForce;
            nearestPlanet = newNearest;
        }

        PVector f = new PVector(0f, 0f);
        // calculate gravity vector
        f.x = nearestPlanet.getPosition().x-obj.getPosition().x;
        f.y = nearestPlanet.getPosition().y-obj.getPosition().y;
        // scale it with distance
        float dist = f.mag() ;
        if(dist != 0)
            f.mult(1f/dist) ;
        else f.mult(0);
        currentGravityForce = f;
        if(previousGravityForce != null)
            currentGravityForce.add(previousGravityForce);
        if(previousGravityForce != null) previousGravityForce.mult(0.992f);
    }

    @Override
    void updateForce(GObject p) {
        if(planets!= null && planets.size() > 0) updateForceAndPlanet();
        obj.integrate(nearestPlanet, currentGravityForce);
    }
}
