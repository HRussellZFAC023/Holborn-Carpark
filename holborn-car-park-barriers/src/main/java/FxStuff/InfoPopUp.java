package FxStuff;

import FxStuff.Controllers.InfoPopUpController;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;

public class InfoPopUp {
    // private AnchorPane blurrAnchor;
    private AnchorPane mainAnchor;
    private AnchorPane root;
    private InfoPopUpController infoPopUpController;
    private boolean alreadyOn;
    private boolean debug_mode = false;
    private double blurrRadius = 20;
    private EventHandler<ActionEvent> eventHandler;

    public InfoPopUp(AnchorPane mainAnchor) {
        this.mainAnchor = mainAnchor;
        // this.blurrAnchor = blurrAnchor;
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

    public void show(String message) {
        show(message, true);
    }

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
