package uk.co.holborn.carparkclient;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * The Sprite class represents a quick way of
 * loading and playing sprite sheets on a {@link ImageView}
 * <p></p>
 * The default FPS is set to 60
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class Sprite {
    private ImageView spriteView;
    private Rectangle2D rect;
    private int fps = 60;
    private int count;
    private int columns;
    private int rows;
    private SpriteAnimation currentAnimation;

    /**
     * Initializes a sprite from a String reference to an image.
     * The image will be looked for in the classpath
     * (getClassLoader().getResourceAsStream...)
     * The sheet will be sliced up in to width x height
     * cells.
     *
     * @param spriteView a {@link ImageView} object to set the sprite to
     * @param sheet      a string referencing an image file
     * @param width      the width of a single sprite cell
     * @param height     the height of a single sprite cell
     */
    public Sprite(ImageView spriteView, String sheet, int width, int height) {
        this.spriteView = spriteView;
        setSheet(sheet, width, height);
    }

    /**
     * Initializes a sprite from a Image object.
     * The image will be looked for in the classpath
     * (getClassLoader().getResourceAsStream...)
     * The sheet will be sliced up in to width x height
     * cells.
     *
     * @param spriteView a {@link ImageView} object to set the sprite to
     * @param image      a Image object
     * @param width      the width of a single sprite cell
     * @param height     the height of a single sprite cell
     */
    public Sprite(ImageView spriteView, Image image, int width, int height) {
        this.spriteView = spriteView;
        setImage(image, width, height);
    }

    /**
     * Set a new sprite from a String reference to an image.
     * The image will be looked for in the classpath
     * (getClassLoader().getResourceAsStream...)
     *
     * @param sheet  a string referencing an image file
     * @param width  the width of a single sprite cell
     * @param height the height of a single sprite cell
     */
    public void setSheet(String sheet, int width, int height) {
        setImage(new Image(getClass().getResource(sheet).toExternalForm()), width, height);
    }

    /**
     * Set a new sprite from a Image reference.
     *
     * @param image  a Image object
     * @param width  the width of a single sprite cell
     * @param height the height of a single sprite cell
     */
    public void setImage(Image image, int width, int height) {
        spriteView.setImage(image);
        rect = new Rectangle2D(0, 0, width, height);
        resetView();
        columns = (int) (spriteView.getImage().getWidth() / width);
        rows = (int) (spriteView.getImage().getHeight() / height);
        count = columns * rows;
        if (currentAnimation != null) {
            pause();
            currentAnimation = null;
        }
    }

    /**
     * Play the animation indefinitely
     */
    public void play() {
        play(Animation.INDEFINITE);
    }

    /**
     * Play the animations the specified amount of times
     *
     * @param cycles the number of times the animation will play
     */
    public void play(int cycles) {
        this.currentAnimation = new SpriteAnimation(Duration.seconds((double) (count / fps)), cycles);
        this.currentAnimation.play();
    }

    /**
     * Set the number of sprites that are on a sprite sheet
     * if they are less than (rows * columns)
     *
     * @param count number of sprites on the sprite sheet
     */
    public void setSpritesCount(int count) {
        this.count = count;
    }

    /**
     * Pauses the current animation.
     */
    public void pause() {
        if (currentAnimation != null) currentAnimation.pause();
    }

    /**
     * Replays the current animation from the start.
     */
    public void replay() {
        if (currentAnimation == null) play();
        else currentAnimation.playFromStart();
    }

    /**
     * Set the frames-per-second for future animations. This will not take effect
     * until a new call is made to play or playTimes
     *
     * @param fps the new FPS (default: 60)
     */
    public void setFPS(int fps) {
        this.fps = fps;
    }

    public void resetView() {
        pause();
        spriteView.setViewport(new Rectangle2D(0, 0, rect.getWidth(), rect.getHeight()));
    }


    /**
     * SpriteAnimation helps us transition the frames in a {@link Sprite}
     */
    public class SpriteAnimation extends Transition {

        private int lastIndex;

        /**
         * Initialises a new transition
         *
         * @param duration   the duration of the animation
         * @param cycleCount the number of times the animation will play
         */
        public SpriteAnimation(Duration duration, int cycleCount) {
            setCycleDuration(duration);
            setInterpolator(Interpolator.LINEAR);
            setCycleCount(cycleCount);
        }

        protected void interpolate(double k) {
            final int index = Math.min((int) Math.floor(k * count), count - 1);
            final int offsetX = 0;
            final int offsetY = 0;
            if (index != lastIndex) {
                final int x = (index % columns) * (int) rect.getWidth() + offsetX;
                final int y = (index / columns) * (int) rect.getHeight() + offsetY;
                spriteView.setViewport(new Rectangle2D(x, y, rect.getWidth(), rect.getHeight()));
                lastIndex = index;
            }
        }
    }
}
