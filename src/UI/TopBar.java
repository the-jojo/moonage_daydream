package UI;

import Elements.Astronaut;
import Main.MainGame;
import Main.Res;
import Physics.PlanetSystem;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Top UI bar displaying Player stats and the map
 */
public class TopBar {
    private static final int MSG_TIMER_MAX = 500;
    private static final int RED_TIMER_MAX = 20;

    private MainGame p;
    private float width, height;
    private PVector topLeft;
    private float buf;
    private String message = "Welcome to \"Moonage Daydream\"";
    private int msgTimer = MSG_TIMER_MAX;
    private int redTimer = RED_TIMER_MAX;
    private boolean red = true;

    public TopBar(MainGame p) {
        this.p = p;
    }

    public void display(PVector topLeft, float width, float height){
        this.topLeft = topLeft;
        this.width = width;
        this.height = height;
        buf = width/100f;

        p.stroke(255);
        p.strokeWeight(5);
        p.point(topLeft.x, topLeft.y);

        displayStats();
        displayMap();
        displayMessage();
    }

    /**
     * Shows a string message at the top of the screen for 500 frames
     */
    private void displayMessage(){
        if(msgTimer > 0){
            PVector pos = topLeft.copy().add(1f/5f*width, 2*buf);
            p.textSize(4f*buf);
            p.fill(255, 153, 153);
            p.textAlign(p.LEFT, p.TOP);
            p.text(message, pos.x, pos.y);
            msgTimer--;
        }
    }

    private void displayMap(){
        float w = width * 1f/5.5f;
        float h = width * 1f/8f;
        PVector pos = topLeft.copy().add(width - w - buf, buf);

        // black transparent background
        p.strokeWeight(0);
        p.fill(0,0,0,191);
        p.rect(pos.x, pos.y, w, h);

        PVector start = pos.add(3*buf, 2*buf);

        // constellation name
        p.textSize(3f*buf);
        p.fill(255, 153, 153, 170);
        p.textAlign(p.LEFT, p.CENTER);
        p.text(p.getUniverse().getCurConstellation().getConstellationName(), pos.x - 2*buf, pos.y + h/2 + 1.5f*buf);

        float scale = buf;
        // stars
        for(int i = 0; i <  p.getUniverse().getCurConstellation().getSystemsSmall().length; i++){
            // converts small relative positions to scale to my map
            PVector sPos = p.getUniverse().getCurConstellation().getSystemsSmall()[i];
            PVector mapPos = sPos.copy();
            mapPos.mult(scale);
            mapPos = new PVector(mapPos.x + start.x, mapPos.y + start.y);
            if(p.getUniverse().getCurConstellation().planetSystemComplete(i)){
                // display sun for completed one
                p.image(Res.SUN, mapPos.x, mapPos.y, 14, 14);
            }else{
                // display point for non-completed one
                p.stroke(255, 255, 204);
                p.strokeWeight(7);
                p.point(mapPos.x, mapPos.y);
            }

        }

        // player position as red dot
        PVector pPos = p.getUniverse().getCurConstellation().getPlayerPosSmall();
        pPos.mult(scale);
        pPos = new PVector(pPos.x + start.x, pPos.y + start.y);
        p.stroke(255, 0, 0);
        p.strokeWeight(7);
        p.point(pPos.x, pPos.y);
    }

    private void displayStats(){
        p.imageMode(p.CORNER);
        Astronaut player = p.getUniverse().getAstronaut();
        // life
        PVector lifeImgPos = topLeft.copy().add(buf, buf);
        int lifeSize = Math.round(width * 1f/26f);
        for(int i = 0; i < player.getLifes(); i++){
            p.image(Res.LIFE, lifeImgPos.x, lifeImgPos.y, lifeSize, lifeSize);
            lifeImgPos.add(lifeSize + buf, 0);
        }
        for(int i = player.getLifes(); i < Astronaut.LIFE_MAX; i++){
            p.image(Res.LIFE_X, lifeImgPos.x, lifeImgPos.y, lifeSize, lifeSize);
            lifeImgPos.add(lifeSize + buf, 0);
        }

        // air ( blinks red when less than air <= 10 )
        PVector airImgPos = topLeft.copy().add(buf, buf + lifeSize + buf);
        int airSize = lifeSize;
        p.image(Res.BUBBLE, airImgPos.x, airImgPos.y, airSize, airSize);
        airImgPos.add(airSize + buf, airSize/2 - buf);
        p.textSize(airSize);
        if(player.getAir() > 10){
            p.fill(255);
        }else{
            if(red){
                // show in red
                p.fill(255, 0, 0);
            }else{
                p.fill(255);
            }
            redTimer--;
            if(redTimer <= 0){
                redTimer = RED_TIMER_MAX;
                red = !red;
            }
        }
        p.textAlign(p.LEFT, p.CENTER);
        p.text(player.getAir(), airImgPos.x, airImgPos.y);

        // stars
        if(p.getUniverse().getCurPlanetSystem() != null){
            PVector starImgPos = topLeft.copy().add(buf, 2*buf + lifeSize + airSize + buf);
            int starSize = lifeSize;
            for(int i = 0; i < p.getUniverse().getCurPlanetSystem().getStarsCollected(); i++){
                p.image(Res.STAR, starImgPos.x, starImgPos.y, starSize, starSize);
                starImgPos.add(starSize + buf, 0);
            }
            for(int i = p.getUniverse().getCurPlanetSystem().getStarsCollected(); i < PlanetSystem.STARS_MAX; i++){
                p.image(Res.STAR_X, starImgPos.x, starImgPos.y, starSize, starSize);
                starImgPos.add(starSize + buf, 0);
            }
        }

        PVector frPos = new PVector(topLeft.x + 3* lifeSize + 5*buf, topLeft.y + buf);
        p.textSize(2*buf);
        p.fill(255);
        p.textAlign(p.LEFT, p.TOP);
        p.text(Math.round(p.frameRate), frPos.x, frPos.y);

        // progress
        float progressHeight = lifeSize * 0.6f;
        float progressWidth = lifeSize * 4;
        PVector progressPos = topLeft.copy().add(buf, height - buf - progressHeight);
        p.strokeWeight(0);
        p.fill(155, 155, 155);
        p.rect(progressPos.x, progressPos.y, progressWidth, progressHeight);
        p.fill(249, 214, 12);
        p.rect(progressPos.x, progressPos.y, progressWidth * p.getUniverse().getCurConstellation().getPercCompletedSystems(), progressHeight);
        p.textSize(buf * 2);
        p.fill(0);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("progress", progressPos.x + progressWidth/2, progressPos.y + progressHeight/2);

        p.imageMode(p.CENTER);

    }

    public void setMsg(String msg){
        message = msg;
        msgTimer = MSG_TIMER_MAX;
    }
}
