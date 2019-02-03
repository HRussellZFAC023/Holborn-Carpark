package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import uk.co.holborn.carparkclient.controllers.ConnectionPopUpController;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class ConnectionPopUp {
    private AnchorPane blurrAnchor;
    private AnchorPane mainAnchor;
    private AnchorPane root;
    private ConnectionPopUpController connectionPopUpController;
    private boolean alreadyOn;
    private boolean debug_mode = false;
    private double blurrRadius = 20;

    public ConnectionPopUp(AnchorPane mainAnchor, AnchorPane blurrAnchor) {
        this.mainAnchor = mainAnchor;
        this.blurrAnchor = blurrAnchor;
        alreadyOn = false;
        root = new AnchorPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connection_popup.fxml"));
        try {
            root = loader.load();
            connectionPopUpController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show(String message) {
        if (!debug_mode)
            if (!alreadyOn) {
                setBottomAnchor(root, 0.0);
                setRightAnchor(root, 0.0);
                setLeftAnchor(root, 0.0);
                setTopAnchor(root, 0.0);
                GaussianBlur gb = new GaussianBlur();
                gb.setRadius(0);
                blurrAnchor.setEffect(gb);
                root.setTranslateY(40);
                root.setOpacity(0);
                Platform.runLater(() -> {
                    connectionPopUpController.setText(message);
                    mainAnchor.getChildren().add(root);
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(gb.radiusProperty(), blurrRadius, Interpolator.EASE_BOTH)),
                            new KeyFrame(Duration.seconds(1), new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                            new KeyFrame(Duration.seconds(1), new KeyValue(root.opacityProperty(), 1, Interpolator.EASE_BOTH))
                    );
                    timeline.play();
                });
                alreadyOn = true;
            } else {
                Platform.runLater(() -> {
                    connectionPopUpController.setText(message);
                });
            }


    }

    public void removePopUp() {
        if (!debug_mode)
            if (alreadyOn) {
                GaussianBlur gb = new GaussianBlur();
                gb.setRadius(blurrRadius);
                blurrAnchor.setEffect(gb);
                root.setTranslateY(0);
                root.setOpacity(1);
                Timeline timeline = new Timeline();
                Platform.runLater(() -> {
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(gb.radiusProperty(), 0, Interpolator.EASE_BOTH)),
                            new KeyFrame(Duration.seconds(1.5), new KeyValue(root.translateYProperty(), 40, Interpolator.EASE_BOTH)),
                            new KeyFrame(Duration.seconds(1), new KeyValue(root.opacityProperty(), 0, Interpolator.EASE_BOTH))
                    );
                    timeline.play();
                    timeline.setOnFinished(t -> {
                        mainAnchor.getChildren().remove(root);
                        alreadyOn = false;
                    });
                });
            }
    }
}