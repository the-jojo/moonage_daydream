package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class Bubble extends GObject {
    private static final int MAX_PLANET_DIST = 70;

    private int maxTtl;
    private int life;

    public Bubble(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public Bubble(MainGame pa) {
        super(pa);
    }

    @Override
    public void display() {
        super.display();

        life--;
    }

    @Override
    protected void setup() {
        pic = Res.BUBBLE;
        floatFactor = p.random(-0.03f, -0.09f);
        height = 40;
        width = 40;
        maxTtl = (int) p.random(100, 400);
        life = maxTtl;
    }

    @Override
    public void integrate(Planet nearestPlanet, PVector gravity) {
        super.integrate(nearestPlanet, gravity);

        // bounce around a small distance away from planet
        if(nearestPlanet.getPosition().dist(position) - nearestPlanet.getRadius() > MAX_PLANET_DIST){
            PVector relPosToPlanet = new PVector(position.x - nearestPlanet.getPosition().x, position.y - nearestPlanet.getPosition().y);
            float h = (p.radians(90) + relPosToPlanet.heading());
            PVector force = new PVector(0, (nearestPlanet.getPosition().dist(position) - nearestPlanet.getRadius() - MAX_PLANET_DIST)*1.8f);
            force.rotate(h);
            forceAccumulator.add(force);
            //velocity.mult(0.6f);
            //height = 20;
        }
    }

    public boolean exists(){
        return life > 0;
    }

    public void destroy(){
        life = 0;
    }
}
