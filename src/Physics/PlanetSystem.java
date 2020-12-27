package Physics;

import Elements.*;
import Main.MainGame;
import UI.KeyHint;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class PlanetSystem {
    public static final int STARS_MAX = 3;

    protected MainGame p;
    protected ForceRegistry forceRegistry;

    protected List<Planet> planets;
    protected Astronaut astronaut;
    protected SpaceCraft spaceCraft;
    protected SatelliteDish satelliteDish;
    protected List<Bubble> bubbles = new ArrayList<>();
    protected List<Monster> monsters = new ArrayList<>();
    protected List<Star> stars = new ArrayList<>();
    protected boolean enteredSpaceCraft = false;
    protected int starsCollected = 0;

    public PlanetSystem(MainGame p, List<Planet> planets, List<Monster> enemies) {
        this.p = p;
        this.planets = planets;
        this.monsters = enemies;

        forceRegistry = new ForceRegistry();

        for(Monster m: monsters){
            forceRegistry.add(m, new Gravity(planets, m));
        }

    }


    public void display(){
        // find planets ready to get
        List<Planet> readyToGet = new ArrayList<>();
        planets.forEach(pl -> {
            if(pl.readyToGet())
                readyToGet.add(pl);
        });
        // display 1 readyToGetPlanet first
        if(!readyToGet.isEmpty()){
            readyToGet.get(0).display(true);
        }
        // display all planets
        for (Planet p: planets){
            p.display(false);
        }

        // show player objects
        spaceCraft.display();
        if(!enteredSpaceCraft && astronaut.getLifes() > 0)
            astronaut.display();
        if(astronaut.getLifes() == 0){
            p.getUniverse().setMessage("You died. Please restart the game to \nstart again.");
        }

        // show satellite dish (if any)
        if(satelliteDish != null){
            satelliteDish.display();
        }

        // show bubbles
        for(int i = 0; i < bubbles.size(); i++){
            if(bubbles.get(i).exists()){
                bubbles.get(i).display();
            }else {
                bubbles.remove(i);
                i--;
            }
        }

        // show stars
        for(int i = 0; i < stars.size(); i++){
            if(stars.get(i).exists()){
                stars.get(i).display();
            }else {
                stars.remove(i);
                i--;
            }
        }

        // show monsters
        for(int i = 0; i < monsters.size(); i++){
            if(monsters.get(i).exists()){
                monsters.get(i).display();
            }else {
                monsters.remove(i);
                i--;
            }
        }
    }

    /**
     * Returns the center of the system (average of all planet positions)
     * @return
     */
    public PVector getCentre(){
        PVector c = new PVector(0, 0);
        for (Planet p: planets) {
            c.add(p.getPosition());
        }
        c.div(planets.size());
        return c;
    }

    /**
     * Shows key hints for player
     * @param hint_s
     * @param hint_space
     */
    public void handleKeyHints(KeyHint hint_s, KeyHint hint_space){
        if(astronaut.getLifes() > 0) {
            if(satelliteDish != null && !enteredSpaceCraft && astronaut.isOnPlanet() && astronaut.getPosition().dist(satelliteDish.getPosition()) <= satelliteDish.getWidth() / 2){
                PVector d = new PVector(0f, -astronaut.getHeight() / 2 - 40);
                d.rotate(astronaut.getHeading());
                hint_s.setPosition(astronaut.getPosition().copy().add(d));
                hint_s.display();
            } else if (!enteredSpaceCraft && astronaut.isOnPlanet() && astronaut.getPosition().dist(spaceCraft.getPosition()) <= spaceCraft.getWidth() / 2) {
                PVector d = new PVector(0f, -astronaut.getHeight() / 2 - 40);
                d.rotate(astronaut.getHeading());
                hint_s.setPosition(astronaut.getPosition().copy().add(d));
                hint_s.display();
            } else if (enteredSpaceCraft && spaceCraft.isOnPlanet()) {
                PVector d = new PVector(0f, -spaceCraft.getHeight() / 2 - 40);
                d.rotate(spaceCraft.getHeading());
                hint_s.setPosition(spaceCraft.getPosition().copy().add(d));
                hint_s.display();
            } else if (!enteredSpaceCraft && astronaut.isOnPlanet() && !astronaut.getNearestPlanet().hasGrass()) {
                PVector d = new PVector(0f, -astronaut.getHeight() / 2 - 40);
                d.rotate(astronaut.getHeading());
                hint_space.setPosition(astronaut.getPosition().copy().add(d));
                hint_space.display();
            }
        }
    }

    public void addBubble(Bubble bubble){
        forceRegistry.add(bubble, new Gravity(planets, bubble));
        bubbles.add(bubble);
    }

    public void addStar(Star star){
        forceRegistry.add(star, new Gravity(planets, star));
        stars.add(star);
    }

    public void addAstronaut(Astronaut astronaut){
        forceRegistry.add(astronaut, new Gravity(planets, astronaut));
        this.astronaut = astronaut;
    }

    public void addSpaceCraft(SpaceCraft craft){
        forceRegistry.add(craft, new Gravity(planets, craft));
        this.spaceCraft = craft;
    }

    public void updateForces(){
        forceRegistry.updateForces();
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public List<Bubble> getBubbles() {
        return bubbles;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public List<Star> getStars() {
        return stars;
    }

    public boolean isEnteredSpaceCraft() {
        return enteredSpaceCraft;
    }

    public void setEnteredSpaceCraft(boolean enteredSpaceCraft) {
        this.enteredSpaceCraft = enteredSpaceCraft;
    }

    public void removeAstronautAndSpaceCraft(){
        forceRegistry.remove(spaceCraft);
        forceRegistry.remove(astronaut);
        spaceCraft = null;
        astronaut = null;
    }

    public void addStarCollected(){
        starsCollected++;
    }

    public int getStarsCollected(){
        return starsCollected;
    }

    public void addSatelliteDish(SatelliteDish dish){
        forceRegistry.add(dish, new Gravity(planets, dish));
        satelliteDish = dish;
    }

    public SatelliteDish getSatelliteDish() {
        return satelliteDish;
    }
}
