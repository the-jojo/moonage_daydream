package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class Star extends GObject {

    boolean exists = true;

    public Star(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public Star(MainGame pa) {
        super(pa);
    }

    @Override
    public void display() {
        super.display();
    }

    @Override
    protected void setup() {
        pic = Res.STAR;
        floatFactor = 0.75f;
        height = 40;
        width = 40;
    }

    @Override
    public void integrate(Planet nearestPlanet, PVector gravity) {
        super.integrate(nearestPlanet, gravity);

        PVector relPosToPlanet = new PVector(position.x - nearestPlanet.getPosition().x, position.y - nearestPlanet.getPosition().y);
        float h = (p.radians(90) + relPosToPlanet.heading());


        // stick to planet
        if(almostTouchesPlanet(getMiddleOfLowerEdge(), nearestPlanet)){
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
        }
    }

    public boolean exists(){
        return exists;
    }

    public void destroy(){
        exists = false;
    }
}
