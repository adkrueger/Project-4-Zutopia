import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;

/**
 * Class that implements a ball with a position and velocity.
 */
public class Ball {
    // Constants
    /**
     * The radius of the ball.
     */
    public static final int BALL_RADIUS = 8;
    /**
     * The initial velocity of the ball in the x direction.
     */
    public static final double INITIAL_VX = 1e-7;
    /**
     * The initial velocity of the ball in the y direction.
     */
    public static final double INITIAL_VY = 1e-7;
    /**
     * The acceleration multiplier of the ball
     */
    public static final double SPEEDACC = 0.1;

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
    public Circle getCircle() {
        return circle;
    }

    /**
     * Sets the list of target shapes to the list given as a parameter
     * @param newShapes the new target shapes
     */
    public void setShapes(ArrayList<Rectangle> newShapes) {
        shapes = newShapes;
    }

    /**
     * Creates a new GameImpl to be used for creating the game
     * @param newGameImpl the new instance of a game implementation
     */
    public void setGameApp(GameImpl newGameImpl) {
        gameImpl = newGameImpl;
    }

    /**
     * Creates a new Paddle for the player to control
     * @param newPaddle the new Paddle
     */
    public void setPaddle(Paddle newPaddle) {
        paddle = newPaddle;
    }

    /**
     * resets the bottomHits value to its original value
     * (necessary when playing multiple games in a row)
     */
    public void resetBottomHits() {
        bottomHits = 0;
    }

    /**
     * Returns the current value of bottomHits
     * @return the current value of bottomHits
     */
    public int getBottomHits() {
        return bottomHits;
    }

    /**
     * Resets the numShapesLeft variable to its original value of 16
     * (necessary when starting a new game)
     */
    public void resetNumShapesLeft() {
        numShapesLeft = 16;
    }

    /**
     * Returns the current value of numShapesLeft
     * @return the current value of numShapesLeft
     */
    public int getNumShapesLeft() {
        return numShapesLeft;
    }

    /**
     * Resets the helperShapesLeft variable to its original value of 17
     * (necessary when starting a new game)
     */
    public void resetHelperShapesLeft() {
        helperShapesLeft = 17;
    }

    /**
     * Constructs a new Ball object at the centroid of the game board
     * with a default velocity that points down and right.
     */
    public Ball() {
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
    public void updatePosition(long deltaNanoTime) {

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
            bottomHits++;    // adds 1 to bottomHits, which ends the game when it equals 5
        } else if (y - circle.getRadius() < 0 && vy < 0) {
            vy = -vy;
        }

    }

    /**
     * Checks whether the ball is colliding with a Rectangle,
     * which is removed assuming the Rectangle that the ball collided
     * with is an animal. Otherwise, the ball bounces off of the Rectangle
     * (which would have to be the Paddle)
     * @param shape the Rectangle that the ball is colliding with
     * @param isPaddle whether or not the Rectangle is the Paddle
     * @return the list of Rectangles to be removed from the game
     */
    private Rectangle checkColliding(Rectangle shape, boolean isPaddle) {
        Rectangle remove = null;
        Bounds circleBounds = circle.getBoundsInParent();
        Bounds shapeBounds = shape.getBoundsInParent();

        if (circleBounds.intersects(shapeBounds)) {
            double left = Math.abs(x - shapeBounds.getMinX());
            double right = Math.abs(x - shapeBounds.getMaxX());
            double top = Math.abs(y - shapeBounds.getMaxY());
            double bottom = Math.abs(y - shapeBounds.getMinY());

            if (Math.abs(y - shape.getY()) > Math.abs(x - shape.getX()) || isPaddle) {
                System.out.println("Top/Bottom hit");
                vy = -vy;
                if (top < bottom) {
                    y = shapeBounds.getMaxY() + BALL_RADIUS;
                } else {
                    y = shapeBounds.getMinY() - BALL_RADIUS;
                }
            } else {
                System.out.println("Left/Right hit");
                vx = -vx;
                if (left < right) {
                    x = shapeBounds.getMinX() - BALL_RADIUS;
                } else {
                    x = shapeBounds.getMaxX() + BALL_RADIUS;
                }
            }
            if (!isPaddle) {
                gameImpl.getChildren().remove(shape);
                remove = shape;
                numShapesLeft--;    // removes 1 from numShapesLeft, which ends the game when it is 0
            }
        }
        return remove;
    }
}
