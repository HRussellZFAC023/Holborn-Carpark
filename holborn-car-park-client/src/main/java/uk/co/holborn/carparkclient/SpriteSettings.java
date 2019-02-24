package uk.co.holborn.carparkclient;

import javafx.scene.image.Image;

public class SpriteSettings {
    private String path;
    private double scaleToWidth;
    private double scaleToHeight;
    private int slices;
    private int count;
    private int FPS;
    private Image image;

    SpriteSettings(String path, double scale, int slices, int count, int FPS) {
        this.path = path;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = count;
        this.FPS = FPS;
    }

    SpriteSettings(String path, double scale, int slices, int FPS) {
        this.path = path;
        this.scaleToWidth = scale;
        this.scaleToHeight = scale;
        this.slices = slices;
        this.count = slices*slices;
        this.FPS = FPS;
    }

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
