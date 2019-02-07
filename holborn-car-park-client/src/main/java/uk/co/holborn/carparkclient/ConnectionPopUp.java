package uk.co.holborn.carparkclient;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.ConnectionPopUpController;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;

public class ConnectionPopUp {
   // private AnchorPane blurrAnchor;
    private AnchorPane mainAnchor;
    private AnchorPane root;
    private ConnectionPopUpController connectionPopUpController;
    private boolean alreadyOn;
    private boolean debug_mode = false;
    private double blurrRadius = 20;
    private EventHandler<ActionEvent> eventHandler;

    public ConnectionPopUp(AnchorPane mainAnchor) {
        this.mainAnchor = mainAnchor;
       // this.blurrAnchor = blurrAnchor;
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
                    Timeline t = new Timeline();
                    Animator.nodeThrowIn_Keyframes(root,t);
                    t.play();
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
                    Timeline t = new Timeline();
                    Animator.nodeBringOut_KeyFrames(root,t);
                    t.setOnFinished(ev->{
                        mainAnchor.getChildren().remove(root);
                        alreadyOn = false;
                    });
                    t.play();
                });
            }
    }
}
