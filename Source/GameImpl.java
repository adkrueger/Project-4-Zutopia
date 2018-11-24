import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameImpl extends Pane implements Game {
    /**
     * Defines different states of the game.
     */
    public enum GameState {
        WON, LOST, ACTIVE, NEW
    }

    // Constants
    /**
     * The width of the game board.
     */
    public static final int WIDTH = 400;
    /**
     * The height of the game board.
     */
    public static final int HEIGHT = 600;

    // Instance variables
    private static Ball ball;
    private static Paddle paddle;
    private static ArrayList<Rectangle> collidingShapes = new ArrayList<>();

    /**
     * Constructs a new GameImpl.
     */
    public GameImpl() {
        setStyle("-fx-background-color: white;");

        restartGame(GameState.NEW);
    }

    public String getName() {
        return "Zutopia";
    }

    public Pane getPane() {
        return this;
    }

    private void restartGame(GameState state) {
        getChildren().clear();  // remove all components from the game

        // Create and add ball
        ball = new Ball();
        getChildren().add(ball.getCircle());  // Add the ball to the game board
        ball.setGameApp(this);

        int picScale = WIDTH / 5;

        for (int i = 0; i < 4; i++) {
            for (int l = 0; l < 4; l++) {
                int random = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                Image img = new Image("file:horse.jpg");
                if (random == 1) {
                    img = new Image("file:duck.jpg");
                }
                if(random == 2)
                {
                    img = new Image("file:goat.jpg");
                }
                Rectangle animal = new Rectangle(i * picScale + picScale/2, l * picScale + picScale/2, picScale * 0.8, picScale * 0.8);
                animal.setFill(new ImagePattern(img));
                getChildren().add(animal);
                collidingShapes.add(animal);
            }
        }

        // Create and add paddle
        paddle = new Paddle();
        getChildren().add(paddle.getRectangle());  // Add the paddle to the game board

        ball.setPaddle(paddle);
        ball.setShapes(collidingShapes);

        // Add start message
        final String message;
        if (state == GameState.LOST) {
            message = "Game Over\n" + ball.getBottomHits();
        } else if (state == GameState.WON) {
            message = "You won!\n" + ball.getNumShapesLeft();
        } else {
            message = "";
        }
        final Label startLabel = new Label(message + "Click mouse to start");
        startLabel.setLayoutX(WIDTH / 2 - 50);
        startLabel.setLayoutY(HEIGHT / 2 + 100);
        getChildren().add(startLabel);

        // Add event handler to start the game
        setOnMouseClicked(e -> {
            GameImpl.this.setOnMouseClicked(null);

            // As soon as the mouse is clicked, remove the startLabel from the game board
            getChildren().remove(startLabel);
            run();
        });

        // Add another event handler to steer paddle...
        setOnMouseMoved(e -> {
            paddle.updatePosition(e.getX(), e.getY());
        });
    }

    /**
     * Begins the game-play by creating and starting an AnimationTimer.
     */
    public void run() {

        ball.resetBottomHits();
        ball.resetNumShapesLeft();
        ball.resetNumShapesLeft();

        // Instantiate and start an AnimationTimer to update the component of the game.
        new AnimationTimer() {
            private long lastNanoTime = -1;

            public void handle(long currentNanoTime) {
                if (lastNanoTime >= 0) {  // Necessary for first clock-tick.
                    GameState state;
                    if ((state = runOneTimestep(currentNanoTime - lastNanoTime)) != GameState.ACTIVE) {
                        // Once the game is no longer ACTIVE, stop the AnimationTimer.
                        stop();
                        // Restart the game, with a message that depends on whether
                        // the user won or lost the game.
                        restartGame(state);
                    }
                }
                // Keep track of how much time actually transpired since the last clock-tick.
                lastNanoTime = currentNanoTime;
            }
        }.start();
    }

    /**
     * Updates the state of the game at each timestep. In particular, this method should
     * move the ball, check if the ball collided with any of the animals, walls, or the paddle, etc.
     *
     * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
     * @return the current game state
     */
    public GameState runOneTimestep(long deltaNanoTime) {
        ball.updatePosition(deltaNanoTime);

        if (ball.getBottomHits() >= 5) {
            restartGame(GameState.LOST);
        }

        if (ball.getNumShapesLeft() <= 0) {
            restartGame(GameState.WON);
        }

        return GameState.ACTIVE;
    }
}
