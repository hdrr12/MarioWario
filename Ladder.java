import bagel.*;
import bagel.util.*;

import java.util.ArrayList;

public class Ladder {
    private final Image image;
    private final Point position;

    public Ladder(double x, double y) {
        image = new Image("res/ladder.png");

        position = new Point(x, y);
    }

    // place ladders
    public void draw() {
        image.draw(position.x, position.y);
    }

    // ladder hitbox
    public Rectangle getBox() {
        return image.getBoundingBoxAt(position);
    }

    // access ladder position
    public Point getPos() {
        return position;
    }
}
