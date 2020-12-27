package Elements;

import Main.MainGame;
import processing.core.PVector;

public abstract class PlayableGObject extends GObject {

    protected boolean upPressed = false;
    protected boolean leftPressed = false;
    protected boolean rightPressed = false;

    public PlayableGObject(MainGame p, PVector position, PVector velocity, float invMass) {
        super(p, position, velocity, invMass);
    }

    public PlayableGObject(MainGame pa) {
        super(pa);
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }
}
