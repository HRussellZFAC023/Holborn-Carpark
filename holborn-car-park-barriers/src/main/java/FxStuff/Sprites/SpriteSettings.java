/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package FxStuff.Sprites;

import javafx.scene.image.Image;

/**
 * The SpriteSettings class implements a easy way of
 * setting all the necessary parameters for a sprite. T
 * he SpriteSettings class expects a path to an image with 1:1 ratio (same width and height).
 *
 * @author Vlad Alboiu
 * @version 1.0.0
 */
@SuppressWarnings({"ALL", "SameParameterValue"})
public class SpriteSettings {
    private String path;
    private double scaleToWidth;
    private double scaleToHeight;
    private int slices;
    private int count;
    private int FPS;
    private int offsetX;
    private int offsetY;
    private Image image;

    /**
     * Initialises a new SpriteSettings instance
     *
     * @param path   the path of the file found under resources folder
     * @param scale  the scale of the image (in pixels, i.e  4096; Note: if no scaling is used just add the original width or height)
     * @param slices the number of slices for a column or row
     * @param count  the number of non empty frames the sprite has
     * @param FPS    the speed at which the animation will be ran at
     */
    SpriteSettings(String path, double scale, int slices, int count, int FPS) {
        this.path = path;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = count;
        this.FPS = FPS;
    }

    /**
     * Initialises a new SpriteSettings instance
     *
     * @param path   the path of the file found under resources folder
     * @param scale  the scale of the image (in pixels, i.e  4096; Note: if no scaling is used just add the original width or height)
     * @param slices the number of slices for a column or row
     * @param count  the number of non empty frames the sprite has
     * @param offset the offset to shift the image  with (both on x and y)
     * @param FPS    the speed at which the animation will be ran at
     */
    SpriteSettings(String path, double scale, int slices, int count, int offset, int FPS) {
        this.path = path;
        this.offsetX = offset;
        this.offsetY = offset;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = count;
        this.FPS = FPS;
    }

    /**
     * Initialises a new SpriteSettings instance
     *
     * @param path    the path of the file found under resources folder
     * @param scale   the scale of the image (in pixels, i.e  4096; Note: if no scaling is used just add the original width or height)
     * @param slices  the number of slices for a column or row
     * @param count   the number of non empty frames the sprite has
     * @param offsetX the offset to shift the image  with in the x axis
     * @param offsetY the offset to shift the image  with in the y axis
     * @param FPS     the speed at which the animation will be ran at
     */
    SpriteSettings(String path, double scale, @SuppressWarnings("SameParameterValue") int slices, int count, int offsetX, int offsetY, int FPS) {
        this.path = path;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = count;
        this.FPS = FPS;
    }

    /**
     * Initialises a new SpriteSettings instance
     *
     * @param path   the path of the file found under resources folder
     * @param scale  the scale of the image (in pixels, i.e  4096; Note: if no scaling is used just add the original width or height)
     * @param slices the number of slices for a column or row
     * @param FPS    the speed at which the animation will be ran at
     */
    SpriteSettings(String path, double scale, int slices, int FPS) {
        this.path = path;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = slices * slices;
        this.FPS = FPS;
    }

    //just getters and setters

    String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getScaleToWidth() {
        return scaleToWidth;
    }

    public void setScaleToWidth(double scaleToWidth) {
        this.scaleToWidth = scaleToWidth;
    }

    public double getScaleToHeight() {
        return scaleToHeight;
    }

    public void setScaleToHeight(double scaleToHeight) {
        this.scaleToHeight = scaleToHeight;
    }

    public void setScaleTo(int scale) {
        this.scaleToHeight = scale;
        this.scaleToWidth = scale;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getCount() {
        return count;
    }

    public void getCount(int count) {
        this.count = count;
    }

    public int getSlices() {
        return slices;
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }

    public int getFPS() {
        return FPS;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }
}
