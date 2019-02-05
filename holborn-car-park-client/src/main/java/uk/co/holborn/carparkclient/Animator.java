package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animator {
    public static void nodeFade(Node node, boolean in, double time){
        double opacityEnd, opacityStart;
        Timeline timeline = new Timeline();
        if(!in) {
            opacityStart = 1;
            opacityEnd = 0;
            node.setScaleX(1);
            node.setScaleY(1);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(time), new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(time), new KeyValue(node.scaleYProperty(), 0, Interpolator.EASE_IN))
            );
        }else {
            opacityStart = 0;
            opacityEnd = 1;
            node.setScaleX(0.5);
            node.setScaleY(0.5);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(time), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(time), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_IN))
            );
        }
        node.setOpacity(opacityStart);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(time), new KeyValue(node.opacityProperty(), opacityEnd, Interpolator.EASE_IN))
        );
        timeline.play();
    }
    public static void nodeFade(Node node, boolean in){
       nodeFade(node, in, 0.5);
    }
}
