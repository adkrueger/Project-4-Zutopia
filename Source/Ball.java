import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Class that implements a ball with a position and velocity.
 */
class Ball {
    // Constants
    /**
     * The radius of the ball.
     */
    private static final int BALL_RADIUS = 8;
    /**
     * The initial velocity of the ball in the x direction.
     */
    private static final double INITIAL_VX = 1e-7;
    /**
     * The initial velocity of the ball in the y direction.
     */
    private static final double INITIAL_VY = 1e-7;
    /**
     * The acceleration multiplier of the ball
     */
    private static final double SPEEDACC = 0.2;

    // Instance variables
    // (x,y) is the position of the center of the ball.
    private int bottomHits = 0;
    private int numShapesLeft = 16;
    private int helperShapesLeft = 16;
    private double speedMult = 1.0;
    private double x, y;
    private double vx, vy;
    private Circle circle;
    private Paddle paddle;
    private GameImpl gameImpl;
    private ArrayList<Rectangle> shapes;

    /**
     * @return the Circle object that represents the ball on the game board.
     */
    Circle getCircle() {
        return circle;
    }

    /**
     * Sets the list of target shapes to the list given as a parameter
     *
     * @param newShapes the new target shapes
     */
    void setShapes(ArrayList<Rectangle> newShapes) {
        shapes = newShapes;
    }

    /**
     * Creates a new GameImpl to be used for creating the game
     *
     * @param newGameImpl the new instance of a game implementation
     */
    void setGameApp(GameImpl newGameImpl) {
        gameImpl = newGameImpl;
    }

    /**
     * Creates a new Paddle for the player to control
     *
     * @param newPaddle the new Paddle
     */
    void setPaddle(Paddle newPaddle) {
        paddle = newPaddle;
    }

    /**
     * Returns the current value of bottomHits
     *
     * @return the current value of bottomHits
     */
    int getBottomHits() {
        return bottomHits;
    }

    /**
     * Returns the current value of numShapesLeft
     *
     * @return the current value of numShapesLeft
     */
    int getNumShapesLeft() {
        return numShapesLeft;
    }

    /**
     * Constructs a new Ball object at the centroid of the game board
     * with a default velocity that points down and right.
     */
    Ball() {
        x = GameImpl.WIDTH / 2.0;
        y = GameImpl.HEIGHT / 5.0 * 4;
        vx = INITIAL_VX;
        vy = INITIAL_VY;

        circle = new Circle(BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
        circle.setLayoutX(x - BALL_RADIUS);
        circle.setLayoutY(y - BALL_RADIUS);
        circle.setFill(Color.BLACK);
    }

    /**
     * Updates the position of the ball, given its current position and velocity,
     * based on the specified elapsed time since the last update.
     *
     * @param deltaNanoTime the number of nanoseconds that have transpired since the last update
     */
    void updatePosition(long deltaNanoTime) {

        wallHit(x, y);

        if (getNumShapesLeft() < helperShapesLeft) {     // accelerates the ball when it removes a shape
            speedMult += SPEEDACC;
            helperShapesLeft--;
        }

        double dx = vx * deltaNanoTime * speedMult;
        double dy = vy * deltaNanoTime * speedMult;

        x += dx;
        y += dy;

        circle.setTranslateX(x - (circle.getLayoutX() + BALL_RADIUS));
        circle.setTranslateY(y - (circle.getLayoutY() + BALL_RADIUS));


    }

    /**
     * Tests whether the ball collided with a wall, reversing its x-direction if
     * it hits a vertical wall, or reversing its y-direction if it hits a horizontal
     * wall
     * Also adjusts the number of times the ball can hit the bottom
     * wall before the game is considered over
     *
     * @param x
     * @param y
     */
    private void wallHit(double x, double y) {
        if (shapes != null) {
            ArrayList<Rectangle> removeList = new ArrayList<>();
            for (Rectangle rectangle : shapes) {
                removeList.add(checkColliding(rectangle, false));
            }
            shapes.removeAll(removeList);
        }

        if (paddle != null) {
            checkColliding(paddle.getRectangle(), true);
        }

        if (x + circle.getRadius() > GameImpl.WIDTH && vx > 0) {
            vx = -vx;
        } else if (x - circle.getRadius() < 0 && vx < 0) {
            vx = -vx;
        }
        if (y + circle.getRadius() > GameImpl.HEIGHT && vy > 0) {
            // Hit the bottom of the screen
            vy = -vy;
            bottomHits++;    // adds 1 to bottomHits, which ends the game when it equals 5 (starts at 0)
            if (bottomHits == 5) {
                shapes.clear();     // when the player loses the game, removes all shapes so they don't count for the next game
            }
            System.out.println(bottomHits);
        } else if (y - circle.getRadius() < 0 && vy < 0) {
            vy = -vy;
        }

    }

    /**
     * Checks whether the ball is colliding with a Rectangle,
     * which is removed assuming the Rectangle that the ball collided
     * with is an animal. Otherwise, the ball bounces off of the Rectangle
     * (which would have to be the Paddle)
     *
     * @param shape    the Rectangle that the ball is colliding with
     * @param isPaddle whether or not the Rectangle is the Paddle
     * @return the list of Rectangles to be removed from the game
     */
    private Rectangle checkColliding(Rectangle shape, boolean isPaddle) {
        Rectangle remove = null;
        Bounds circleBounds = circle.getBoundsInParent();
        Bounds shapeBounds = shape.getBoundsInParent();

        if (circleBounds.intersects(shapeBounds)) {
            double top = Math.abs(y - shapeBounds.getMaxY());
            double bottom = Math.abs(y - shapeBounds.getMinY());
            vy = -vy;
            if (top < bottom) {
                y = shapeBounds.getMaxY() + BALL_RADIUS;
            } else {
                y = shapeBounds.getMinY() - BALL_RADIUS;
            }
            if (!isPaddle) {
                gameImpl.getChildren().remove(shape);
                remove = shape;
                numShapesLeft--;    // removes 1 from numShapesLeft, which ends the game when it is 0 (starts at 16)
                System.out.println(numShapesLeft);
            }
        }
        return remove;
    }
}
