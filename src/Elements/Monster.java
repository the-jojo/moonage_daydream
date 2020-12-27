package Elements;

import Main.MainGame;
import Main.Res;
import Physics.Planet;
import processing.core.PVector;

public class Monster extends GObject {
    protected static final int LAST_HIT_MAX = 60;

    protected float moveDir = 0;
    protected boolean exists = true;
    protected int lastHit = 0;
    protected boolean spawnsStar = false;

    public Monster(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public Monster(MainGame pa) {
        super(pa);
    }

    @Override
    protected void setup() {
        height = 60;
        width = 70;
        pic = Res.MONSTER;
        floatFactor = 0.75f;
        moveDir = p.random(-8, 8);
    }

    @Override
    public void display() {
        super.display();

        moveDir = randomDirChange();
        move(moveDir);

        lastHit--;
    }

    /**
     * Moves the monster into the direction given.
     * @param dir
     */
    protected void move(float dir){
        PVector d = new PVector(dir, 0f);
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
     * Randomly increases or decreases the move direction.
     * @return
     */
    protected float randomDirChange(){
        float newDir = moveDir + p.random(-2, 2);
        if(newDir < -10)
            newDir = -10;
        if(newDir > 10)
            newDir = 10;
        return newDir;
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

    /**
     * When coming in contact with a player, move away from the player.
     * @param astronaut
     */
    public void pushBackFromPlayer(Astronaut astronaut){
        if(astronaut.getHeading() < this.getHeading()){
            moveDir = 10;
            move(20);
        }else{
            moveDir = -10;
            move(-20);
        }

        lastHit = LAST_HIT_MAX;
    }

    public boolean exists(){
        return exists;
    }

    public void destroy(){
        exists = false;
        if(spawnsStar){
            PVector d = new PVector(0f, -15);
            d.rotate(p.random(0, p.radians(360)));
            Star star = new Star(p, position.copy(), d, 0.001f);
            p.getUniverse().getCurPlanetSystem().addStar(star);
        }
    }

    public boolean canHitPlayer(){
        return lastHit <= 0;
    }

    public void setSpawnsStar(){
        spawnsStar = true;
    }
}
