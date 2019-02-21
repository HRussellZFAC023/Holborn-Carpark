package uk.co.holborn.carparkclient;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ThemeProvider {
    static Scene scene;
    static Themes currentTheme;

    ThemeProvider(Scene scene, Themes defaultTheme) {
        ThemeProvider.scene = scene;
        switchTheme(defaultTheme);
    }

    public static Themes getCurrentTheme() {
        return currentTheme;
    }

    public static void switchTheme(Themes theme) {
        if (currentTheme == null) {
            currentTheme = theme;
        }
        if (currentTheme != theme) {
            ImageView imgv = getImageFromNode(scene.getRoot(), 2);
            AnchorPane root = (AnchorPane) scene.getRoot();
            root.getChildren().add(imgv);
            imgv.fitWidthProperty().bind(root.widthProperty());
            imgv.fitHeightProperty().bind(root.heightProperty());
            FadeTransition f = new FadeTransition();
            f.setNode(root.getChildren().get(2));
            f.setDuration(Duration.seconds(0.5));
            f.setInterpolator(Interpolator.EASE_BOTH);
            f.setFromValue(1.0);
            f.setToValue(0.0);
            f.setOnFinished(e->{
                root.getChildren().remove(2);
            });
            f.play();
            scene.getStylesheets().remove(currentTheme.getStylesheetPath());
            currentTheme = theme;
        }
        scene.getStylesheets().add(theme.getStylesheetPath());
    }
    public static ImageView getImageFromNode (Node node, double scale) {
        final Bounds bounds = node.getLayoutBounds();
        final WritableImage image = new WritableImage(
                (int) Math.round(bounds.getWidth() * scale),
                (int) Math.round(bounds.getHeight() * scale));
        Rectangle rect = new Rectangle();
        rect.setHeight(bounds.getHeight());
        rect.setWidth(bounds.getWidth());
        node.setClip(rect);
        final SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
        final ImageView view = new ImageView(node.snapshot(spa, image));
        view.setFitWidth(bounds.getWidth());
        view.setFitHeight(bounds.getHeight());
        node.setClip(null);
        return view;
    }
}
