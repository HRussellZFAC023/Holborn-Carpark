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
import uk.co.holborn.carparkclient.controllers.MainViewController;

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
        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connection_popup.fxml"));
            try {
                connectionPopUpController = new ConnectionPopUpController();
                loader.setController(connectionPopUpController);
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void show(String message) {
        if (!debug_mode)
            if (!alreadyOn) {
                setBottomAnchor(root, 0.0);
                setRightAnchor(root, 0.0);
                setLeftAnchor(root, 0.0);
                setTopAnchor(root, 0.0);
                Platform.runLater(() -> {
                    connectionPopUpController.setText(message);
                    mainAnchor.getChildren().add(root);
                    Animator.nodeBlurrBackgroundAndShowPopUp(blurrAnchor, root, null);
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
                Platform.runLater(() -> {
                    Animator.nodeReverseBlurrBackgroundAndHidePopup(blurrAnchor, root, t -> {
                        SceneManager sm = MainViewController.getInstance().sceneManager;
                        sm.changeTo(Scenes.LANDING);
                        sm.clearSceneQueue();
                        mainAnchor.getChildren().remove(root);
                        alreadyOn = false;
                    });
                });
            }
    }
}
