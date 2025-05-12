import bagel.*;
import bagel.util.*;

import java.security.Key;
import java.util.ArrayList;

public class Mario {
    private final Image leftImage;
    private final Image rightImage;
    private final Image hammerLeftImage;
    private final Image hammerRightImage;

    private Point position;
    private Point lastPosition;
    private boolean facingRight = true;
    private boolean hasHammer = false;
    private boolean onLadder = false;
    private boolean onGround = false;
    private final double HORIZONTAL_SPEED = 3.5;
    private final double VERTICAL_SPEED = 3.0;
    private final double GRAVITY = 0.2;
    private final double MAX_FALL_SPEED = 10.0;  // max fall speed for mario
    private double verticalVelocity = 0;

    private ArrayList<Barrel> jumpedBarrels = new ArrayList<>(); //check for barrel jumoed during a single jump
    private boolean isJumping = false;


    public Mario(double x, double y) {
        leftImage = new Image("res/mario_left.png");
        rightImage = new Image("res/mario_right.png");
        hammerLeftImage = new Image("res/mario_hammer_left.png");
        hammerRightImage = new Image("res/mario_hammer_right.png");
        position = new Point(x, y);
        //will be used for previous position for checks for ladders and barrels etc. to get a path basically
        lastPosition = new Point(x, y);
    }

    public void update(Input input, ArrayList<Platform> platforms, ArrayList<Ladder> ladders) {
        // add movement stuff
        //last position for collision detecting
        lastPosition = new Point(position.x, position.y);

        //check if mario on ladder
        onLadder = isOnLadder(ladders);

        if (input.wasPressed(Keys.SPACE) && onGround) {
            verticalVelocity = -5.0;
            onGround = false;
            isJumping = true;
        }

        //apply gravity OR ladder movement
        if (onLadder) {
            //move up or down
            if (input.isDown(Keys.UP)) {
                position = new Point(position.x, position.y - VERTICAL_SPEED);
            } else if (input.isDown(Keys.DOWN)) {
                // Check if there's a platform below before moving down
                boolean platformBelow = isPlatformBelow(platforms);
                if (platformBelow || !isAtBottomOfLadder(ladders)) {
                    position = new Point(position.x, position.y + VERTICAL_SPEED);
                }
            }
            verticalVelocity = 0;
        } else {
            //when not on ladder
            verticalVelocity = Math.min(verticalVelocity + GRAVITY, MAX_FALL_SPEED);
            position = new Point(position.x, position.y + verticalVelocity);
        }

        //handle horizontal movement
        if (input.isDown(Keys.LEFT)) {
            position = new Point(position.x - HORIZONTAL_SPEED, position.y);
            facingRight = false;
        } else if (input.isDown(Keys.RIGHT)) {
            position = new Point(position.x + HORIZONTAL_SPEED, position.y);
            facingRight = true;
        }

        //only check collision if not on ladder
        if (!(onLadder && input.isDown(Keys.DOWN))) {
            handleCollisions(platforms);
        }

        if (isJumping && onGround) {
            isJumping = false;
            jumpedBarrels.clear(); // clear barrels jimped in this jump
        }



        //check screen boundaries
        if (position.x < 0) {
            position = new Point(0, position.y);
        } else if (position.x > Window.getWidth()) {
            position = new Point(Window.getWidth(), position.y);
        }

        // prevent falling off bottom of the screen
        if (position.y > Window.getHeight()) {
            position = new Point(position.x, Window.getHeight());
            verticalVelocity = 0;
        }

    }

    // heck if platform directly mario Mario when on a ladder
    private boolean isPlatformBelow(ArrayList<Platform> platforms) {
        Rectangle marioBox = getBox();
        double marioBottomY = position.y + (marioBox.bottom() - marioBox.top()) / 2;

        for (Platform platform : platforms) {
            Rectangle platformBox = platform.getBox();

            // check if mario is directly above a platform
            if (position.x >= platformBox.left() &&
                    position.x <= platformBox.right() &&
                    marioBottomY <= platformBox.top() &&
                    marioBottomY + VERTICAL_SPEED >= platformBox.top()) {
                return true;
            }
        }
        return false;
    }

    // check if mairo is at the bottom of the ladder
    private boolean isAtBottomOfLadder(ArrayList<Ladder> ladders) {
        Rectangle marioBox = getBox();

        for (Ladder ladder : ladders) {
            Rectangle ladderBox = ladder.getBox();

            if (marioBox.intersects(ladderBox) &&
                    Math.abs(position.x - ladder.getPos().x) < (ladderBox.right() - ladderBox.left()) / 3) {

                // check if at bottom of this ladder
                if (position.y + (marioBox.bottom() - marioBox.top()) / 2 >= ladderBox.bottom() - 5) {
                    return true;
                }
            }
        }
        return false;
    }


    private void handleCollisions(ArrayList<Platform> platforms) {
        onGround = false;
        Rectangle marioBox = getBox();

        //get hitboxes of platforms
        for (Platform platform : platforms) {
            Rectangle platformBox = platform.getBox();

            //if mario is on ladder only check horizontal collison
            //since he can go vertical through platform on ladderc
            if (onLadder) {
                // check mario collide horziontal with platform
                if (position.y >= platformBox.top() &&  //mario is below top of platform
                        position.y <= platformBox.bottom() && // and he is above the bottom therefore being "in" the platform
                        ((lastPosition.x + marioBox.right() / 2 <= platformBox.left() && position.x + marioBox.right() / 2 >= platformBox.left()) || //intersecting left edge
                                (lastPosition.x - marioBox.left() / 2 >= platformBox.right() && position.x - marioBox.left() / 2 <= platformBox.right()))) { //intersecting right edge

                    // stop horizontla movement through plattform
                    if (position.x > lastPosition.x) {
                        //snap mario to left of platform
                        //used marioBox.right/2 becuase original position is for center so this gives edge by minusing half the width
                        position = new Point(platformBox.left() - marioBox.right() / 2, position.y);
                    } else {
                        //snap mario to right of platorm
                        //used .left/2 becuase original position is for center so this gives edge by minusing half the width
                        position = new Point(platformBox.right() + marioBox.left() / 2, position.y);
                    }
                }
                continue; // skip vertical check when on ladder bcs mario can pass throguh ladder
            }

            // for vertical collison when not on ladder
            if (lastPosition.y < platformBox.top() &&
                    position.y + (marioBox.bottom() - marioBox.top()) / 2 >= platformBox.top() &&
                    position.x >= platformBox.left() &&
                    position.x <= platformBox.right()) {

                // land on platform and snap marios feet to the top of platform so its aligned
                position = new Point(position.x, platformBox.top() - (marioBox.bottom() - marioBox.top()) / 2);
                verticalVelocity = 0;
                onGround = true;
            }

            // check if hit platform from below
            else if (lastPosition.y > platformBox.bottom() &&
                    position.y - (marioBox.bottom() - marioBox.top()) / 2 <= platformBox.bottom() &&
                    position.x >= platformBox.left() &&
                    position.x <= platformBox.right()) {

                // stop up movement
                //snap marios head to bottom of platform so it doesnt go through the platform and mimics collision
                position = new Point(position.x, platformBox.bottom() + (marioBox.bottom() - marioBox.top()) / 2);
                verticalVelocity = 0;
            }
        }
    }

    //check if mario is on the ladder return bool
    private boolean isOnLadder(ArrayList<Ladder> ladders) {
        Rectangle marioBox = getBox();
        for (Ladder ladder : ladders) {
            Rectangle ladderBox = ladder.getBox();
            double ladderWidth = ladderBox.right() - ladderBox.left();
            //also check if mario in center -> middle 1/3 of ladder
            if (marioBox.intersects(ladderBox) &&
                    Math.abs(position.x - ladder.getPos().x) < ladderWidth / 3) {
                return true;
            }
        }
        return false;
    }

    public void draw() {
        Image currentImage;

        if (hasHammer) {
            if (facingRight) {
                currentImage = hammerRightImage;
            } else {
                currentImage = hammerLeftImage;
            }
        } else {
            if (facingRight) {
                currentImage = rightImage;
            } else {
                currentImage = leftImage;
            }
        }
        currentImage.draw(position.x, position.y);
    }

    public Rectangle getBox() {
        Image currentImage;
        if (hasHammer){
            if(facingRight){
                currentImage = hammerRightImage;
            }else{
                currentImage = hammerLeftImage;
            }
        }else {
            if (facingRight) {
                currentImage = rightImage;
            }   else  {
                currentImage = leftImage;
            }
        }
        return currentImage.getBoundingBoxAt(position);
    }

    public Point getPos() {
        return position;
    }

    //set state to has hammer
    public void pickUpHammer() {
        hasHammer = true;
    }

    //check if has hammer
    public boolean hasHammer() {
        return hasHammer;
    }

    public boolean jumpedOver(Barrel barrel) {
        Rectangle marioBox = getBox();
        Rectangle barrelBox = barrel.getBox();

        // check if marios center horizontally passed barrel
        boolean crossedHorizontally = (lastPosition.x < barrel.getPos().x && position.x >= barrel.getPos().x) ||
                (lastPosition.x > barrel.getPos().x && position.x <= barrel.getPos().x);

        boolean isAbove = marioBox.bottom() < barrelBox.top();

        // only score if jumping and havent scored barrel in this jump, to prevent multiple point adds while a single jump
        if (isJumping && crossedHorizontally && isAbove && !jumpedBarrels.contains(barrel)) {
            jumpedBarrels.add(barrel); // add to list for only this jump
            return true;
        }
        return false;
    }




}
