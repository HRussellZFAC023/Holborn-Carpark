package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SceneManager {
    private AnchorPane scenePane;
    private HashMap<String, String> scenes;
    private boolean animationFinished ;
    private ArrayList<String> pastStages;

     SceneManager(AnchorPane scenePane, HashMap<String, String> scenes) {
        this.scenePane = scenePane;
        this.scenes = scenes;
        pastStages = new ArrayList<>();
        animationFinished = true;
    }

     void switchToScene(String sceneKey){
        if(animationFinished){
            switchTo(sceneKey);
            animateFadeInOut(false);
        }
    }
    public void goBack(){
        if(animationFinished && pastStages.size() >=2){
            switchTo(pastStages.get(pastStages.size()-2));
            animateFadeInOut(true);
            pastStages.remove(pastStages.size()-1);
        }
    }
    private void switchTo(String sceneKey) {
        try {
            if (scenes.get(sceneKey) != null) {
                AnchorPane root = FXMLLoader.load(getClass().getResource(scenes.get(sceneKey)));
                scenePane.setBottomAnchor(root, 0.0);
                scenePane.setRightAnchor(root, 0.0);
                scenePane.setLeftAnchor(root, 0.0);
                scenePane.setTopAnchor(root, 0.0);
               addSceneToBackQueue(sceneKey);
                scenePane.getChildren().add(root);

            } else {
                System.err.println("There is no object for the given key");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearSceneQueue(){
         pastStages.clear();
    }
    private void addSceneToBackQueue(String sceneKey){
         if(pastStages.size()<2) pastStages.add(sceneKey);
         else {
             if(!pastStages.get(pastStages.size()-2).equals(sceneKey))
                 pastStages.add(sceneKey);
         }


    }
    private void animateFadeInOut(boolean reverse) {
        if(scenePane.getChildren().size()>=2){
            animationFinished = false;
            double duration = 0.3;
            double first_initial_ScaleXY, second_initial_ScaleXY, first_initial_Opacity, second_initial_Opacity, first_end_ScaleXY, second_end_ScaleXY, seconf_final_Opacity,first_final_Opacity;
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
                    animationFinished=true;
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
        timeline.setOnFinished(t -> {
            scenePane.getChildren().remove(0);
        });
        timeline.play();

    }
}
