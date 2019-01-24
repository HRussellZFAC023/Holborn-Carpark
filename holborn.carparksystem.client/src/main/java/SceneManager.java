import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;

public class SceneManager {
    private AnchorPane scenePane;
    private HashMap<String, String > scenes;

    public SceneManager(AnchorPane scenePane, HashMap<String, String > scenes){
        this.scenePane = scenePane;
        this.scenes = scenes;
    }
    public void switchTo(String sceneKey){
        try {
            if(scenes.get(sceneKey)!=null){
                AnchorPane root = FXMLLoader.load(getClass().getResource(scenes.get(sceneKey)));
                double x = root.getPrefWidth();
                scenePane.setBottomAnchor(root, 0.0);
                scenePane.setRightAnchor(root, 0.0);
                scenePane.setLeftAnchor(root, 0.0);
                scenePane.setTopAnchor(root, 0.0);
                scenePane.getChildren().add(root);
                if(scenePane.getChildren().size()==2){
                    scenePane.getChildren().get(1).setTranslateX(x);
                    Timeline timeline = new Timeline();
                    KeyValue kv = new KeyValue(scenePane.getChildren().get(1).translateXProperty(), 0, Interpolator.EASE_OUT);
                    KeyFrame kf = new KeyFrame(Duration.seconds(0.4), kv);
                    AnchorPane ap = (AnchorPane)scenePane.getChildren().get(0);
                    KeyValue kv1 = new KeyValue(scenePane.getChildren().get(0).translateXProperty(), -ap.getPrefWidth(), Interpolator.EASE_OUT);
                    KeyFrame kf1 = new KeyFrame(Duration.seconds(0.4), kv1);
                    timeline.getKeyFrames().addAll(kf, kf1);
                    timeline.setOnFinished(t -> {
                            scenePane.getChildren().remove(0);
                    });
                    timeline.play();
                }
                //After completing animation, remove first scene

            }
            else {
                System.err.println("There is no object for the given key");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
