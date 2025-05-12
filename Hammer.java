import bagel.*;
import bagel.util.*;

public class Hammer {
    private final Image image;
    private Point position;
    private boolean collected = false;

    public Hammer(double x, double y) {
        image = new Image("res/hammer.png");
        position = new Point(x, y);
    }

    public void update(Mario mario) {
        //picking up hammer
        if(!collected && getBox().intersects(mario.getBox())){
            collected = true;
            mario.pickUpHammer();
        }
    }

    //only draw if mario hasnt collected hammer
    public void draw() {
        if (!collected) {
            image.draw(position.x, position.y);
        }
    }

    public Rectangle getBox(){
        return image.getBoundingBoxAt(position);
    }

    public Point getPos() {
        return position;
    }
}
