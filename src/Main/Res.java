package Main;

import processing.core.PImage;

public class Res {
    public static PImage ASTRONAUT;
    public static PImage SPACECRAFT;
    public static PImage MONSTER;
    public static PImage BUBBLE;
    public static PImage STAR;
    public static PImage STAR_X;
    public static PImage LIFE;
    public static PImage LIFE_X;
    public static PImage KEY_S;
    public static PImage KEY_SPACE;
    public static PImage SUN;
    public static PImage SDISH;
    public static PImage[] FIRE = new PImage[4];
    public static PImage[] GRASS = new PImage[9];

    public static void init(MainGame p){
        ASTRONAUT = p.loadImage("austro.png", "png");
        SPACECRAFT = p.loadImage("spaceCraft.png", "png");
        MONSTER = p.loadImage("monster.png", "png");
        BUBBLE = p.loadImage("bubble.png", "png");
        STAR = p.loadImage("star.png", "png");
        STAR_X = p.loadImage("star_x.png", "png");
        LIFE = p.loadImage("heart.png", "png");
        LIFE_X = p.loadImage("heart_x.png", "png");
        KEY_S = p.loadImage("key_S.png", "png");
        KEY_SPACE = p.loadImage("key_space.png", "png");
        SUN = p.loadImage("sun.png", "png");
        SDISH = p.loadImage("sDish.png", "png");
        for (int i = 0; i < 9; i++) {
            GRASS[i] = p.loadImage("grass_" + i + ".png", "png");
        }
        for (int i = 1; i < 5; i++) {
            FIRE[i-1] = p.loadImage("fire_" + i + ".png", "png");
        }
    }
}
