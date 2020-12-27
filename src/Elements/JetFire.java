package Elements;

import Main.MainGame;
import Main.Res;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class JetFire {
    private static final int COUNTER_MAX = 14;
    private MainGame p;
    private PImage[] pics;
    private int width, height;
    private int index = 0;
    private int counter = COUNTER_MAX;

    public JetFire(MainGame pa) {
        this.p = pa;
        width = 29;
        height = 41;
        pics = Res.FIRE;
    }

    public void display(PVector center, float heading){
        p.pushMatrix();
        p.translate(center.x , center.y );
        p.rotate(heading);
        p.image(pics[index], 0, 0, width, height);
        p.popMatrix();
        counter--;
        if(counter < 0){
            counter = COUNTER_MAX;
            index++;
            index = index % 4;
        }
    }
}
