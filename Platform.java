import bagel.*;
import bagel.util.*;

public class Platform {
    private final Image image;
    private final Point position;

    public Platform(double x, double y) {
        image = new Image("res/platform.png");
        position = new Point(x, y);
    }

    // place platform
    public void draw() {
        image.draw(position.x, position.y);
    }

    // platform hitbox
    public Rectangle getBox() {
        return image.getBoundingBoxAt(position);
    }

    // for other classes to get position
    public Point getPos() {
        return position;
    }
}
