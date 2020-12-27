package Main;

import Elements.*;
import Physics.*;
import UI.KeyHint;
import UI.TopBar;
import processing.core.PMatrix2D;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Universe {
    protected MainGame p;

    protected Constellation curConstellation;
    protected PlanetSystem curPlanetSystem;
    protected ForceRegistry forceRegistry;

    protected Astronaut astronaut;
    protected SpaceCraft spaceCraft;
    protected PlayableGObject playable;

    protected PMatrix2D camMat = new PMatrix2D();
    protected float zoomLevel = 1f;
    protected NightSky backgroundSky;
    protected TopBar topBar;

    protected KeyHint hint_s;
    protected KeyHint hint_space;

    protected float currentWidth;
    protected float currentHeight;
    protected PVector curTopLeft;


    public Universe(MainGame pa) {
        p = pa;
        backgroundSky = new NightSky(p);
        topBar = new TopBar(p);
        hint_s = new KeyHint(p, 's');
        hint_space = new KeyHint(p, ' ');
        resetEverything();
    }

    /**
     * Used at the beginning of the game and everytime a sattelite dish is activated and the user travels
     *  to a new constellation
     */
    private void resetEverything(){
        playable = astronaut = new Astronaut(p, new PVector(MainGame.WINDOW_WIDTH/2, 50), new PVector(0f, 0f), 1f/10f);
        spaceCraft = new SpaceCraft(p, new PVector(MainGame.WINDOW_WIDTH/2 - 50, 50), new PVector(0f, 0f), 1f/40f);
        curConstellation = new Constellation(p);
        curPlanetSystem = curConstellation.getStart();
        curPlanetSystem.addSpaceCraft(spaceCraft);
        curPlanetSystem.addAstronaut(astronaut);
    }

    public void display(){
        // adjust camera position and zoom
        if(curPlanetSystem == null || curPlanetSystem.isEnteredSpaceCraft()) {
            if(zoomLevel > 0.7f){
                zoomLevel -= 0.02f;
            }
            camMat = camera(camMat, spaceCraft.getCentrePos().x, spaceCraft.getCentrePos().y, 0f, zoomLevel, zoomLevel);
        } else {
            if(zoomLevel < 1f){
                zoomLevel += 0.02f;
            }
            camMat = camera(camMat, astronaut.getCentrePos().x, astronaut.getCentrePos().y, 0f, zoomLevel, zoomLevel);
        }

        // calculate current width and height
        currentWidth  = MainGame.WINDOW_WIDTH  * (1f/zoomLevel);
        currentHeight = MainGame.WINDOW_HEIGHT * (1f/zoomLevel);
        float topLeftX = playable.getCentrePos().x - currentWidth/2;
        float topLeftY = playable.getCentrePos().y - currentHeight/2;
        curTopLeft = new PVector(topLeftX, topLeftY);

        // update physics
        if(curPlanetSystem != null)
            curPlanetSystem.updateForces();
        else
            forceRegistry.updateForces();

        // show sky background
        backgroundSky.display(curTopLeft, currentWidth, currentHeight);

        // show objects
        if(curPlanetSystem != null) {
            curPlanetSystem.display();
            // show key hints
            curPlanetSystem.handleKeyHints(hint_s, hint_space);
        } else{
            playable.display();
        }

        // show top banner
        topBar.display(curTopLeft, currentWidth, currentHeight);

        // check if we are still close to the current system
        if(curPlanetSystem != null && curPlanetSystem.isEnteredSpaceCraft() && curPlanetSystem.getCentre().dist(playable.getCentrePos()) > currentWidth + 200){
            // left vicinity of closest system
            // remove from last system
            curPlanetSystem.removeAstronautAndSpaceCraft();
            curPlanetSystem = null;

            // free space
            forceRegistry = new ForceRegistry();
            forceRegistry.add(playable, new Gravity(null, playable));
        }
        if(curPlanetSystem == null){
            // check if we can enter next star system
            PlanetSystem newNearest = curConstellation.findNearestSystem(playable);
            if(newNearest.getCentre().dist(playable.getCentrePos()) < currentWidth + 200){
                // switch over
                newNearest.addAstronaut(astronaut);
                newNearest.addSpaceCraft(spaceCraft);
                newNearest.setEnteredSpaceCraft(true);
                curPlanetSystem = newNearest;
            }
        }
    }

    public void addBubble(Bubble bubble){
        curPlanetSystem.addBubble(bubble);
    }

    public List<Planet> getPlanets() {
        return curPlanetSystem.getPlanets();
    }

    /**
     * Handles user key input events
     */
    public void keyPressed(){
        if(astronaut.getLifes() > 0){
            if(p.key == ' '){
                if(curPlanetSystem!= null && !curPlanetSystem.isEnteredSpaceCraft() && astronaut.isOnPlanet() && !astronaut.getNearestPlanet().hasGrass()){
                    astronaut.launchGrow();
                }
            }else if(p.key == 'a'){
                playable.setLeftPressed(true);
            }else if(p.key == 'd'){
                playable.setRightPressed(true);
            }else if(p.key == 'w'){
                playable.setUpPressed(true);
            }else if(p.key == 's'){
                if(curPlanetSystem!= null && curPlanetSystem.getSatelliteDish() != null && !curPlanetSystem.isEnteredSpaceCraft() && astronaut.isOnPlanet() && astronaut.getPosition().dist(curPlanetSystem.getSatelliteDish().getPosition()) <= curPlanetSystem.getSatelliteDish().getWidth() / 2) {
                    // standing next to satellite dish --> enter next constellation
                    Constellation.nextConstIndex(p);
                    resetEverything();
                    setMessage("You were transported to \nthe next constellation!");
                    return;
                }else if(curPlanetSystem!= null && !curPlanetSystem.isEnteredSpaceCraft() && astronaut.getPosition().dist(spaceCraft.getPosition()) <= spaceCraft.getWidth()/2){
                    // standing next to spacecraft --> enter spacecraft
                    curPlanetSystem.setEnteredSpaceCraft(true);
                    // stop playable movement
                    playable.setUpPressed(false);
                    playable.setLeftPressed(false);
                    playable.setRightPressed(false);

                    // set playable as spacecraft
                    playable = spaceCraft;

                }else if(curPlanetSystem!= null && curPlanetSystem.isEnteredSpaceCraft() && spaceCraft.isOnPlanet()){
                    // spacecraft resting on planet --> exit spacecraft
                    curPlanetSystem.setEnteredSpaceCraft(false);
                    // stop playable movement
                    playable.setUpPressed(false);
                    playable.setLeftPressed(false);
                    playable.setRightPressed(false);
                    // reset astronaut position
                    astronaut.setPosition(spaceCraft.getPosition().copy());
                    astronaut.setVelocity(new PVector(0, 0));
                    astronaut.setHeading(spaceCraft.getHeading());
                    // set playable as austronaut
                    playable = astronaut;
                }

            }
        }

    }

    public void keyReleased(){
        if(astronaut.getLifes() > 0) {
            if (p.key == 'a') {
                playable.setLeftPressed(false);
            } else if (p.key == 'd') {
                playable.setRightPressed(false);
            } else if (p.key == 'w') {
                playable.setUpPressed(false);
            }
        }
    }

    /**
     * Magic camera method which moves camera to look at a section of play area. also sets zoom and angle.
     * @param cameraMatrix
     * @param tx
     * @param ty
     * @param angle
     * @param zoomW
     * @param zoomH
     * @return
     */
    private PMatrix2D camera(PMatrix2D cameraMatrix,
                     float tx, float ty, float angle,
                     float zoomW, float zoomH) {

        // Shift the camera to look at screen center.
        cameraMatrix.set(
                1.0f, 0.0f, MainGame.WINDOW_WIDTH * 0.5f,
                0.0f, 1.0f, MainGame.WINDOW_HEIGHT * 0.5f);
        cameraMatrix.rotate(-angle);
        cameraMatrix.scale(zoomW, zoomH);
        cameraMatrix.translate(-tx, -ty);

        // Set this PApplet renderer's matrix.
        p.setMatrix(cameraMatrix);
        return cameraMatrix;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public Astronaut getAstronaut() {
        return astronaut;
    }

    public List<Bubble> getBubbles() {
        return curPlanetSystem.getBubbles();
    }

    public void setCurPlanetSystem(PlanetSystem curPlanetSystem) {
        this.curPlanetSystem = curPlanetSystem;
    }

    public PlanetSystem getCurPlanetSystem() {
        return curPlanetSystem;
    }

    public Constellation getCurConstellation() {
        return curConstellation;
    }

    public PVector getPlayerPos(){
        return playable.getCentrePos();
    }

    public float getCurrentWidth() {
        return currentWidth;
    }

    public float getCurrentHeight() {
        return currentHeight;
    }

    public PVector getCurTopLeft() {
        return curTopLeft;
    }

    public void focusCameraOn(PVector position){
        camMat = camera(camMat, position.x, position.y, 0f, 1, 1);

    }

    public void unfocusCamera(){
        camMat = camera(camMat, spaceCraft.getCentrePos().x, spaceCraft.getCentrePos().y, 0f, zoomLevel, zoomLevel);

    }

    public List<Monster> getMonsters(){
        return curPlanetSystem.getMonsters();
    }

    public List<Star> getStars(){
        return curPlanetSystem.getStars();
    }

    public void setMessage(String message){
        topBar.setMsg(message);
    }
}
