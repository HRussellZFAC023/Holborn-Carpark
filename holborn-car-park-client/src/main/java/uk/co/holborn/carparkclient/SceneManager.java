package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static javafx.scene.layout.AnchorPane.*;

/**
 * Scene manager handles the UI changes
 */
public class SceneManager {
    private Logger logger;
    private AnchorPane scenePane;
    private boolean animationFinished;
    private ArrayList<Scenes> pastStages;
    private AnchorPane sc;
    public SceneManager(AnchorPane scenePane) {
        logger = LogManager.getLogger(getClass().getName());
        this.scenePane = scenePane;
        pastStages = new ArrayList<>();
        animationFinished = true;
    }

    public void switchToScene(Scenes scene) {
        if (animationFinished) {
            Platform.runLater(() -> {
                switchTo(scene);
                animateFadeInOut(false);
            });
        }
    }

    public void goBack() {
        if (animationFinished && pastStages.size() >= 2) {
            switchTo(pastStages.get(pastStages.size() - 2));
            animateFadeInOut(true);
            pastStages.remove(pastStages.size() - 1);
        }
    }

    private void switchTo(Scenes scene) {
        if (scene != null) {
//            try {
//                scenesInstance.put(sceneKey, null);
//              scene =  FXMLLoader.load(getClass().getResource(scenes.get(sceneKey)));
//            } catch (IOException e) {
//                logger.trace(e.getStackTrace());
//            }
            sc = scene.getScene();

            setBottomAnchor(sc, 0.0);
            setRightAnchor(sc, 0.0);
            setLeftAnchor(sc, 0.0);
            setTopAnchor(sc, 0.0);
//            scenesInstance.put(sceneKey, root);
            scenePane.getChildren().add(sc);
            addSceneToBackQueue(scene);

        } else {
            System.err.println("There is no object for the given key");
        }

    }

    public void clearSceneQueue() {
        pastStages.clear();
    }

    private void addSceneToBackQueue(Scenes scene) {
        if (pastStages.size() < 2) pastStages.add(scene);
        else {
            if (!pastStages.get(pastStages.size() - 2).equals(scene))
                pastStages.add(scene);
        }


    }

    private void animateFadeInOut(boolean reverse) {
        if (scenePane.getChildren().size() >= 2) {
            animationFinished = false;
            double duration = 0.3;
            double first_initial_ScaleXY, second_initial_ScaleXY, first_initial_Opacity, second_initial_Opacity, first_end_ScaleXY, second_end_ScaleXY, seconf_final_Opacity, first_final_Opacity;
            first_initial_Opacity = 1;
            first_final_Opacity = 0;
            second_initial_Opacity = 0;
            seconf_final_Opacity = 1;
            first_initial_ScaleXY = 1;
            second_initial_ScaleXY = 0;

            if (!reverse) {
                first_end_ScaleXY = 2;
                second_end_ScaleXY = 1;
            } else {
                second_initial_ScaleXY = 2;
                first_end_ScaleXY = 0;
                second_end_ScaleXY = 1;
            }
            if (scenePane.getChildren().size() == 2) {
                Timeline timeline = new Timeline();

                Node firstSceneNode = scenePane.getChildren().get(0);
                Node secondSceneNode = scenePane.getChildren().get(1);

                firstSceneNode.setOpacity(first_initial_Opacity);
                secondSceneNode.setOpacity(second_initial_Opacity);
                firstSceneNode.setScaleX(first_initial_ScaleXY);
                firstSceneNode.setScaleY(first_initial_ScaleXY);
                secondSceneNode.setScaleX(second_initial_ScaleXY);
                secondSceneNode.setScaleY(second_initial_ScaleXY);

                KeyValue firstScene_keyValueOpacity = new KeyValue(firstSceneNode.opacityProperty(), first_final_Opacity, Interpolator.EASE_OUT);
                KeyFrame firstScene_opacityKeyframe = new KeyFrame(Duration.seconds(duration), firstScene_keyValueOpacity);

                KeyValue secondScene_keyValueOpacity = new KeyValue(secondSceneNode.opacityProperty(), seconf_final_Opacity, Interpolator.EASE_OUT);
                KeyFrame secondScene_opacityKeyframe = new KeyFrame(Duration.seconds(duration), secondScene_keyValueOpacity);

                KeyValue firstScene_keyValueScaleX = new KeyValue(firstSceneNode.scaleXProperty(), first_end_ScaleXY, Interpolator.EASE_OUT);
                KeyFrame firstScene_ScaleXKeyframe = new KeyFrame(Duration.seconds(duration), firstScene_keyValueScaleX);
                KeyValue firstScene_keyValueScaleY = new KeyValue(firstSceneNode.scaleYProperty(), first_end_ScaleXY, Interpolator.EASE_OUT);
                KeyFrame firstScene_ScaleYKeyframe = new KeyFrame(Duration.seconds(duration), firstScene_keyValueScaleY);

                KeyValue secondScene_keyValueScaleX = new KeyValue(secondSceneNode.scaleXProperty(), second_end_ScaleXY, Interpolator.EASE_OUT);
                KeyFrame secondScene_ScaleXKeyframe = new KeyFrame(Duration.seconds(duration), secondScene_keyValueScaleX);
                KeyValue secondScene_keyValueScaleY = new KeyValue(secondSceneNode.scaleYProperty(), second_end_ScaleXY, Interpolator.EASE_OUT);
                KeyFrame secondScene_ScaleYKeyframe = new KeyFrame(Duration.seconds(duration), secondScene_keyValueScaleY);


                timeline.getKeyFrames().addAll(
                        firstScene_opacityKeyframe,
                        firstScene_ScaleXKeyframe,
                        firstScene_ScaleYKeyframe,
                        secondScene_opacityKeyframe,
                        secondScene_ScaleXKeyframe,
                        secondScene_ScaleYKeyframe);
                timeline.setOnFinished(t -> {
                    scenePane.getChildren().remove(0);
                    sc = null;
                    System.gc();
                    animationFinished = true;
                });
                timeline.play();
            }
        }

    }

    private void animateFromSide(AnchorPane root) {
        Timeline timeline = new Timeline();

        scenePane.getChildren().get(1).setTranslateX(root.getPrefWidth());
        KeyValue kv = new KeyValue(scenePane.getChildren().get(1).translateXProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.3), kv);

        AnchorPane ap = (AnchorPane) scenePane.getChildren().get(0);
        KeyValue kv1 = new KeyValue(scenePane.getChildren().get(0).translateXProperty(), -ap.getPrefWidth(), Interpolator.EASE_OUT);
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.3), kv1);
        timeline.getKeyFrames().addAll(kf, kf1);
        timeline.setOnFinished(t -> scenePane.getChildren().remove(0));
        timeline.play();

    }
}
