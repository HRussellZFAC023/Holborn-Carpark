package uk.co.holborn.carparkclient;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.InfoPopUpController;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;

/**
 * Class that provides a easy way to instantiate a information popup
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class InfoPopUp {
    private AnchorPane mainAnchor;
    private AnchorPane root;
    private InfoPopUpController infoPopUpController;
    private boolean alreadyOn;
    private boolean debug_mode = false;

    /**
     * Initialising the popup
     *
     * @param mainAnchor the anchor the popup will be a child of
     */
    public InfoPopUp(AnchorPane mainAnchor) {
        this.mainAnchor = mainAnchor;
        alreadyOn = false;
        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/info_popup.fxml"));
            try {
                infoPopUpController = new InfoPopUpController();
                loader.setController(infoPopUpController);
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Display the popup with the given string. If the popup is is already shown,
     * it will display the new given string
     *
     * @param message the message to be displayed
     */
    public void show(String message) {
        show(message, true);
    }

    /**
     * Display the popup with the given string. If the popup is is already shown,
     * it will display the new given string.
     *
     * @param message   the message to be displayed
     * @param indicator whether or not to display an indicator
     */
    public void show(String message, boolean indicator) {
        if (!debug_mode)
            if (!alreadyOn) {
                setBottomAnchor(root, 0.0);
                setRightAnchor(root, 0.0);
                setLeftAnchor(root, 0.0);
                setTopAnchor(root, 0.0);
                Platform.runLater(() -> {
                    infoPopUpController.setText(message);
                    infoPopUpController.setIndicatorVisible(indicator);
                    mainAnchor.getChildren().add(root);
                    Timeline t = new Timeline();
                    Animator.nodeThrowIn_Keyframes(root, t);
                    t.play();
                });
                alreadyOn = true;
            } else {
                Platform.runLater(() -> {
                    infoPopUpController.setText(message);
                    infoPopUpController.setIndicatorVisible(indicator);
                });
            }


    }

    /**
     * Remove the popup from the parent
     */
    public void removePopUp() {
        if (!debug_mode)
            if (alreadyOn) {
                Platform.runLater(() -> {
                    Timeline t = new Timeline();
                    Animator.nodeBringOut_KeyFrames(root, t);
                    t.setOnFinished(ev -> {
                        mainAnchor.getChildren().remove(root);
                        alreadyOn = false;
                    });
                    t.play();
                });
            }
    }
}
