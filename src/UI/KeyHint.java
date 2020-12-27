package UI;

import Main.MainGame;
import Main.Res;
import processing.core.PImage;
import processing.core.PVector;

public class KeyHint {
    public static final int SIZE = 40;

    protected MainGame p;

    protected PImage pic;

    protected char key;
    protected PVector position;

    public KeyHint(MainGame p, char key) {
        this.p = p;
        this.key = key;
        switch (key){
            case 's':
                pic = Res.KEY_S;
                break;
            case ' ':
                pic = Res.KEY_SPACE;
                break;
        }
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public void display(){
        if(pic != null){
            p.tint(255, 127);  // Display at half opacity
            p.image(pic, position.x, position.y, SIZE, SIZE);
            p.tint(255, 255);  // Display at half opacity

        }
    }
}
