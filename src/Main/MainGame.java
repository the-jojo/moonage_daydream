package Main;

import Physics.Constellation;
import processing.core.PApplet;
import processing.core.PFont;

public class MainGame extends PApplet {

    // screen config
    public static final int WINDOW_HEIGHT = 950;
    public static final int WINDOW_WIDTH = 1400;
    // fonts
    public static PFont font_norm;

    protected static Universe universe;

    private static boolean getterMutex = true;

    public void setup(){
        System.out.println("Setup called");
        frameRate(40);
        System.out.println("loading images...");
        Res.init(this);
        System.out.println("creating fonts...");
        font_norm = createFont("Lobster Two",40);
        textFont(font_norm);
        surface.setTitle("Moonage Daydream");
        System.out.println("creating universe...");
        Constellation.randomConstIndex(this);
        universe = new Universe(this);
        imageMode(CENTER);
        System.out.println("done");
    }

    public void settings(){
        System.out.println("Settings called");

        size(WINDOW_WIDTH, WINDOW_HEIGHT);

    }

    public void draw(){
        // background
        background(0, 0, 26);

        universe.display();
        getterMutex = true;
    }

    public void keyPressed(){
        universe.keyPressed();
    }

    public void keyReleased(){
        universe.keyReleased();
    }

    public void mousePressed(){

    }

    public static void main(String[] args) {
        PApplet.main("Main.MainGame");
    }

    public Universe getUniverse() {
        return universe;
    }

    public static boolean requestGetterMutex(){
        if(getterMutex){
            getterMutex = false;
            return true;
        }else{
            return false;
        }
    }
}
