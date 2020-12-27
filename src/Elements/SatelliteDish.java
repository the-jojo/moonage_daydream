package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class SatelliteDish extends GObject {

    public SatelliteDish(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public SatelliteDish(MainGame pa) {
        super(pa);
    }

    @Override
    protected void setup() {
        height = 100;
        width = 90;
        pic = Res.SDISH;
        floatFactor = 1f;
    }

    @Override
    public void display() {
        super.display();
    }

    @Override
    public void integrate(Planet nearestPlanet, PVector gravity) {
        super.integrate(nearestPlanet, gravity);

        PVector relPosToPlanet = new PVector(position.x - nearestPlanet.getPosition().x, position.y - nearestPlanet.getPosition().y);
        float h = (p.radians(90) + relPosToPlanet.heading());

        // point upwards from nearest planet
        turnTowards(h);

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


}
