/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkbarriers.Sprites;

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
 * The default FPS is set to 60
 *
 * @author Vlad Alboiu
 * @version 1.0.3
 * @see Sprites
 * @see SpriteSettings
 */
public class Sprite {
    private final ImageView spriteView;
    private Rectangle2D rect;
    private int fps = 60;
    private int count;
    private int columns;
    private SpriteAnimation currentAnimation;
    private int offsetX;
    private int offsetY;

    /**
     * Initializes a sprite and its parameters from a {@link SpriteSettings} object
     *
     * @param spriteView     a {@link ImageView} object to set the sprite to
     * @param spriteSettings the swttings from which to load the image and parameters
     */
    public Sprite(ImageView spriteView, SpriteSettings spriteSettings) {
        this.spriteView = spriteView;
        setSpriteSettings(spriteSettings);
    }

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
     * @deprecated use {@link #Sprite(ImageView, SpriteSettings)} instead
     */
    public Sprite(ImageView spriteView, String sheet, double width, double height) {
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
     * @deprecated use {@link #Sprite(ImageView, SpriteSettings)} instead
     */
    public Sprite(ImageView spriteView, Image image, double width, double height) {
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
    private void setSheet(String sheet, double width, double height) {
        setImage(new Image(getClass().getResource(sheet).toExternalForm()), width, height);
    }


    /**
     * Set a new sprite from a Image reference.
     *
     * @param image  a Image object
     * @param width  the width of a single sprite cell
     * @param height the height of a single sprite cell
     */
    private void setImage(Image image, double width, double height) {
        setImage(image, width, height, (int) (spriteView.getImage().getWidth() / width));
    }

    /**
     * Set a new sprite from a Image reference.
     *
     * @param image  a Image object
     * @param width  the width of a single sprite cell
     * @param height the height of a single sprite cell
     */
    private void setImage(Image image, double width, double height, int cells) {
        spriteView.setImage(image);
        rect = new Rectangle2D(0, 0, width, height);
        resetView();
        columns = cells;
        int rows = (int) (spriteView.getImage().getHeight() / height);
        count = columns * rows;
        if (currentAnimation != null) {
            pause();
            currentAnimation = null;
        }
    }

    /**
     * Play the animation indefinitely
     */
    private void play() {
        play(Animation.INDEFINITE);
    }

    /**
     * Play the animations the specified amount of times
     *
     * @param cycles the number of times the animation will play
     */
    private void play(int cycles) {
        this.currentAnimation = new SpriteAnimation(Duration.seconds((double) (count / fps)), cycles);
        this.currentAnimation.play();
    }

    /**
     * Set the number of sprites that are on a sprite sheet
     * if they are less than (rows * columns)
     *
     * @param count number of sprites on the sprite sheet
     */
    private void setSpritesCount(int count) {
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
     * Replays the current animation from the start a number of times
     *
     * @param cycles the number of times to be played
     */
    public void replay(int cycles) {
        play(cycles);
    }

    /**
     * Set the frames-per-second for future animations. This will not take effect
     * until a new call is made to play or playTimes
     *
     * @param fps the new FPS (default: 60)
     */
    private void setFPS(int fps) {
        this.fps = fps;
    }

    /**
     * Reset the view to the first frame
     */
    public void resetView() {
        pause();
        if (spriteView != null) spriteView.setViewport(new Rectangle2D(0, 0, rect.getWidth(), rect.getHeight()));
    }

    /**
     * Set the Sprite with the given sprite settings
     *
     * @param spriteSettings the settings to be set too
     */
    public void setSpriteSettings(SpriteSettings spriteSettings) {
        setImage(spriteSettings.getImage(),
                spriteSettings.getScaleToWidth() / spriteSettings.getSlices(),
                spriteSettings.getScaleToHeight() / spriteSettings.getSlices(),
                spriteSettings.getSlices()
        );
        setFPS(spriteSettings.getFPS());
        setSpritesCount(spriteSettings.getCount());
        setOffset(spriteSettings.getOffsetX(), spriteSettings.getOffsetY());
        resetView();
    }

    /**
     * Set the offset the sprite will have
     *
     * @param offsetX the shifting on x axis
     * @param offsetY the shifting on y axis
     */
    private void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * SpriteAnimation helps us transition the frames in a {@link Sprite}
     */
    class SpriteAnimation extends Transition {

        private int lastIndex;

        /**
         * Initialises a new transition
         *
         * @param duration   the duration of the animation
         * @param cycleCount the number of times the animation will play
         */
        SpriteAnimation(Duration duration, int cycleCount) {
            setCycleDuration(duration);
            setInterpolator(Interpolator.LINEAR);
            setCycleCount(cycleCount);
        }

        /**
         * Set the viewport from one sprite cell to another
         *
         * @param k animation progress (0.0 at the start and 1.0 at the end frame)
         */
        protected void interpolate(double k) {
            final int index = Math.min((int) Math.floor(k * count), count - 1);
            final int altOffsetX = offsetX;
            final int aldOffsetY = offsetY;
            if (index != lastIndex) {
                final double x = (index % columns) * rect.getWidth() + offsetX;
                final double y = (double) (index / columns) * rect.getHeight() + offsetY;
                spriteView.setViewport(new Rectangle2D(x, y, rect.getWidth(), rect.getHeight()));
                lastIndex = index;
            }
        }
    }
}
