package uk.co.holborn.carparkclient;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * The ThemeProvider class provides a way to change the
 * application stylesheets during runtime, fading between the themes on change.
 *It requires a {@link Scene} containing only one {@link Pane} as a child (the pane can have more children) for this to work.
 *
 * @author Vlad Alboiu
 * @version 1.0
 * @see Themes
 */
public class ThemeProvider {
    private  Scene scene;
    private  Themes currentTheme;
    private static ThemeProvider instance;

    /**
     * Constructor
     * @param scene the main scene
     * @param defaultTheme the default theme to be shown at launch
     */
    ThemeProvider(Scene scene, Themes defaultTheme){
        this.scene = scene;
        instance = this;
        switchTheme(defaultTheme);
    }

    /**
     * Gets the current theme
     * @return current theme
     */
    public Themes getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Switch do the provided theme, fading into the new one
     * @param theme the theme to switch to
     */
    public  void switchTheme(Themes theme) {
        if (currentTheme == null) {
            currentTheme = theme;
        }else
        if (currentTheme != theme) {
            ImageView imageView = getImageViewFromNode(scene.getRoot(), 2);
            Pane root = (Pane) scene.getRoot();

            imageView.fitWidthProperty().bind(root.widthProperty());
            imageView.fitHeightProperty().bind(root.heightProperty());
            root.getChildren().add(imageView);

            FadeTransition f = new FadeTransition();
            f.setNode(root.getChildren().get(1));
            f.setDuration(Duration.seconds(0.3));
            f.setInterpolator(Interpolator.EASE_BOTH);
            f.setFromValue(1.0);
            f.setToValue(0.0);
            f.setOnFinished(e->{
                root.getChildren().remove(1);
            });
            f.play();

            scene.getStylesheets().remove(currentTheme.getStylesheetPath());
            currentTheme = theme;
        }
        scene.getStylesheets().add(theme.getStylesheetPath());
    }

    /**
     * Get the ThemeProvider instance
     * @return the instance
     */
    public static ThemeProvider getInstance() {
        return instance;
    }

    /**
     *This method gets a {@link ImageView} taken from a node snapshot.
     * Before taking the snapshot, we apply a rectangle clipping mask
     * with the same width and height as the provided node.
     * This is to prevent image shifting due to out-of-bounds
     * excess of effects (i.e. drop shadow that extends outside the node)
     *
     * @param node the node to snapshot
     * @param scale is the image scale (recommended 1 or above for a higher resolution capture)
     * @return ImageView
     */
    private ImageView getImageViewFromNode(Node node, double scale) {
        final Bounds bounds = node.getLayoutBounds();
        final WritableImage image = new WritableImage(
                (int) Math.round(bounds.getWidth() * scale),
                (int) Math.round(bounds.getHeight() * scale));
        Rectangle clippingRect = new Rectangle();
        clippingRect.setHeight(bounds.getHeight());
        clippingRect.setWidth(bounds.getWidth());
        node.setClip(clippingRect);

        final SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(javafx.scene.transform.Transform.scale(scale, scale));

        final ImageView view = new ImageView(node.snapshot(spa, image));
        view.setFitWidth(bounds.getWidth());
        view.setFitHeight(bounds.getHeight());

        node.setClip(null);
        return view;
    }
}
