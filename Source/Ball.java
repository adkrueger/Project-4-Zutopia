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

    // Instance variables
    // (x,y) is the position of the center of the ball.
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

    // TODO: JAVADOC
    public void setShapes(ArrayList<Rectangle> newShapes) {
        shapes = newShapes;
    }

    // TODO: JAVADOC
    public void setGameApp(GameImpl newGameImpl) {
        gameImpl = newGameImpl;
    }

    // TODO: JAVADOC
    public void setPaddle(Paddle newPaddle) {
        paddle = newPaddle;
    }

    /**
     * Constructs a new Ball object at the centroid of the game board
     * with a default velocity that points down and right.
     */
    public Ball() {
        x = GameImpl.WIDTH / 2;
        y = GameImpl.HEIGHT / 5 * 4;
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

        double dx = vx * deltaNanoTime;
        double dy = vy * deltaNanoTime;

        x += dx;
        y += dy;

        circle.setTranslateX(x - (circle.getLayoutX() + BALL_RADIUS));
        circle.setTranslateY(y - (circle.getLayoutY() + BALL_RADIUS));
    }

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
        } else if (y - circle.getRadius() < 0 && vy < 0) {
            vy = -vy;
        }

    }

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
            }
        }
        return remove;
    }
}
