package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class SpaceCraft extends PlayableGObject {

    private static final int radius = 40;

    protected JetFire fireLeft;
    protected JetFire fireRight;
    protected JetFire fireCenter;

    public SpaceCraft(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public SpaceCraft(MainGame pa) {
        super(pa);
    }

    @Override
    protected void setup() {
        pic = Res.SPACECRAFT;
        floatFactor = 0.7f;
        height = 100;
        width = 110;
        fireLeft = new JetFire(p);
        fireRight = new JetFire(p);
        fireCenter = new JetFire(p);
    }

    @Override
    public void display() {
        if(leftPressed){
            PVector down = new PVector(0f, height/2+ 20);
            PVector left = new PVector(width/2 , 0f);
            down.rotate(heading);
            left.rotate(heading);
            PVector pos = position.copy().add(down).add(left);
            fireLeft.display(pos, heading - p.radians(180 + 30));
        }
        if(rightPressed){
            PVector down = new PVector(0f, height/2+ 20);
            PVector right = new PVector(-width/2, 0f);
            down.rotate(heading);
            right.rotate(heading);
            PVector pos = position.copy().add(down).add(right);
            fireRight.display(pos, heading - p.radians(180 - 30));
        }
        if(upPressed){
            PVector down = new PVector(0f, height/2 + 20);
            down.rotate(heading);
            PVector pos = position.copy().add(down);
            fireCenter.display(pos, heading - p.radians(180));
        }

        super.display();

        if(leftPressed){
            left();
        }
        if(rightPressed){
            right();
        }
        if(upPressed){
            up();
        }
    }

    private void right() {
        heading += 0.05f;
    }

    private void left() {
        heading -= 0.05f;
    }

    private void up() {
        PVector d = new PVector(0f, -1f);
        d.rotate(heading);
        velocity.add(d);
    }

    @Override
    public void integrate(Planet nearestPlanet, PVector gravity) {
        super.integrate(nearestPlanet, gravity);

        if(nearestPlanet!= null && gravity != null){
            // stick to surface of planet
            PVector lowerPos = new PVector(0f, radius);
            PVector p_tmp = new PVector(position.x - nearestPlanet.getPosition().x, position.y - nearestPlanet.getPosition().y);
            float headingOfPlanet = (p.radians(90) + p_tmp.heading());
            lowerPos.rotate(headingOfPlanet);
            lowerPos.add(position);

            if((upPressed && touchesPlanet(lowerPos, nearestPlanet)) || (!upPressed && almostTouchesPlanet(lowerPos, nearestPlanet))){
                PVector relPos = new PVector(
                        lowerPos.x - nearestPlanet.getPosition().x,
                        lowerPos.y - nearestPlanet.getPosition().y);
                Double newX = (nearestPlanet.getPosition().x + nearestPlanet.getRadius()*Math.sin(p.radians(90) + (relPos.heading())));
                Double newY = (nearestPlanet.getPosition().y - nearestPlanet.getRadius()*Math.cos(p.radians(90) + (relPos.heading())));
                PVector newPos = new PVector(newX.floatValue(), newY.floatValue());

                PVector translateVector = getPosition().copy().sub(lowerPos);
                newPos.add(translateVector);

                position.x = newPos.x;
                position.y = newPos.y;
                velocity.x = 0;
                velocity.y = 0;
                onPlanet = true;
            }
        }
    }
}
