import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class Paddle {
	// Constants
	/**
	 * The width of the paddle.
	 */
	private static final int PADDLE_WIDTH = 100;

	/**
	 * Half the width of the paddle
	 */
	private static final float HALF_PADDLE_WIDTH = PADDLE_WIDTH / 2.0f;

	/**
	 * The height of the paddle.
	 */
	private static final int PADDLE_HEIGHT = 10;
	/**
	 * The initial position (specified as a fraction of the game height) of center of the paddle.
	 */
	private static final double INITIAL_Y_LOCATION_FRAC = 0.8;
	/**
	 * The minimum position (specified as a fraction of the game height) of center of the paddle.
	 */
	private static final double MIN_Y_LOCATION_FRAC = 0.7;
	/**
	 * The maximum position (specified as a fraction of the game height) of center of the paddle.
	 */
	private static final double MAX_Y_LOCATION_FRAC = 0.9;

	// Instance variables
	private Rectangle rectangle;

	/**
	 * Constructs a new Paddle whose vertical center is at INITIAL_Y_LOCATION_FRAC * GameImpl.HEIGHT.
	 */
	Paddle () {
        final double y = INITIAL_Y_LOCATION_FRAC * GameImpl.HEIGHT;
		rectangle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT);
		rectangle.setLayoutX((double) HALF_PADDLE_WIDTH - HALF_PADDLE_WIDTH);
		rectangle.setLayoutY(y - PADDLE_HEIGHT / 2.0f);
		rectangle.setStroke(Color.GREEN);
		rectangle.setFill(Color.GREEN);
	}

	/**
	 * @return the Rectangle object that represents the paddle on the game board.
	 */
	Rectangle getRectangle () {
		return rectangle;
	}

	/**
	 * Moves the paddle so that its center is at (newX, newY), subject to
	 * the horizontal constraint that the paddle must always be completely visible
	 * and the vertical constraint that its y coordiante must be between MIN_Y_LOCATION_FRAC
	 * and MAX_Y_LOCATION_FRAC times the game height.
	 * @param newX the newX position to move the center of the paddle.
	 * @param newY the newX position to move the center of the paddle.
	 */
    private void moveTo(double newX, double newY) {
		if (newX < HALF_PADDLE_WIDTH) {
			newX = HALF_PADDLE_WIDTH;
		} else if (newX > GameImpl.WIDTH - HALF_PADDLE_WIDTH) {
			newX = GameImpl.WIDTH - HALF_PADDLE_WIDTH;
		}

		if (newY < MIN_Y_LOCATION_FRAC * GameImpl.HEIGHT) {
			newY = MIN_Y_LOCATION_FRAC * GameImpl.HEIGHT;
		} else if (newY > MAX_Y_LOCATION_FRAC * GameImpl.HEIGHT) {
			newY = MAX_Y_LOCATION_FRAC * GameImpl.HEIGHT;
		}

		rectangle.setTranslateX(newX - (rectangle.getLayoutX() + HALF_PADDLE_WIDTH));
		rectangle.setTranslateY(newY - (rectangle.getLayoutY() + PADDLE_HEIGHT / 2.0f));
	}

	/**
	 * Updates the position of the paddle based on the mouse position
	 */
    void updatePosition(double mouseX, double mouseY) {
	    moveTo(mouseX, mouseY);
	}
}
