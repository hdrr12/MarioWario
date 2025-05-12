import bagel.*;
import bagel.util.*;
import java.util.ArrayList;

public class DonkeyKong {
    private final Image image;
    private Point position;
    private boolean falling = true;
    private final double GRAVITY = 0.2;
    private final double MAX_FALL_SPEED = 5.0;
    private double verticalVelocity = 0;

    public DonkeyKong(double x, double y) {
        image = new Image("res/donkey_kong.png");
        position = new Point(x, y);
    }

    public void update(ArrayList<Platform> platforms) {
        // Handle falling onto platform
        if (falling) {
            //gravity with terminal velocity limit
            verticalVelocity = Math.min(verticalVelocity + GRAVITY, MAX_FALL_SPEED);
            position = new Point(position.x, position.y + verticalVelocity);

            // check if barrel landed on a platform
            Rectangle donkeyBox = getBox();
            for (Platform platform : platforms) {
                Rectangle platformBox = platform.getBox();

                if (donkeyBox.bottom() >= platformBox.top() &&
                        donkeyBox.bottom() <= platformBox.bottom() &&
                        position.x >= platformBox.left() &&
                        position.x <= platformBox.right()) {

                    // if landed
                    position = new Point(position.x, platformBox.top() - image.getHeight()/2);
                    verticalVelocity = 0;
                    falling = false;
                    break;
                }
            }
        }
    }

    public void draw() {
        image.draw(position.x, position.y);
    }

    public Rectangle getBox() {
        return image.getBoundingBoxAt(position);
    }

}
