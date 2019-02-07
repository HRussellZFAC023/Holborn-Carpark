package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class Animator {
    public static void nodeFade(Node node, boolean in, double keyFrameTime) {
        double opacityEnd, opacityStart;
        Timeline timeline = new Timeline();
        if (!in) {
            opacityStart = 1;
            opacityEnd = 0;
            node.setScaleX(1);
            node.setScaleY(1);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(keyFrameTime), new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(keyFrameTime), new KeyValue(node.scaleYProperty(), 0, Interpolator.EASE_IN))
            );
        } else {
            opacityStart = 0;
            opacityEnd = 1;
            node.setScaleX(0);
            node.setScaleY(0);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(keyFrameTime), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(keyFrameTime), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_IN))
            );
        }
        node.setOpacity(opacityStart);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(keyFrameTime), new KeyValue(node.opacityProperty(), opacityEnd, Interpolator.EASE_IN))
        );
        timeline.play();
    }

    public static void nodeFade(Node node, boolean in) {
        nodeFade(node, in, 0.5);
    }

    public static void nodeBlurr(Node node) {
        nodeBlurr(node, 60, 0.3);
    }

    public static void nodeBlurr(Node node, double blurrRadius, double keyFrameTime) {
        Timeline timeline = new Timeline();
        blurr_keyframes(node, blurrRadius, keyFrameTime,  timeline);
        timeline.play();

    }


    public static void nodeBlurrBackgroundAndShowPopUp(Node background, Node popup, EventHandler<ActionEvent> eventEventHandler) {
        Timeline timeline = new Timeline();
        double blurrEndKey = 0.4;
        blurr_keyframes(background, 60, blurrEndKey, timeline);
        nodePopInCard_keyframes(popup, blurrEndKey+0.3, timeline);
        timeline.setOnFinished(eventEventHandler);
        timeline.play();
    }

    public static void nodeReverseBlurrBackgroundAndHidePopup(Node background, Node popup, EventHandler<ActionEvent> eventEventHandler) {
        Timeline timeline = new Timeline();
        double blurrEndKey = 0.4;
        nodePushOutCard_keyframes(popup, blurrEndKey, timeline);
        reverseBlurr_keyframes(background, 60, blurrEndKey+0.3, timeline);
        timeline.setOnFinished(eventEventHandler);
        timeline.play();
    }

    public static void blurr_keyframes(Node node, double blurrRadius, double keyFrame, Timeline timeline) {
        GaussianBlur gb = new GaussianBlur();
        gb.setRadius(0);
        node.setEffect(gb);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(keyFrame), new KeyValue(gb.radiusProperty(), blurrRadius, Interpolator.EASE_IN))
        );
    }
    public static void reverseBlurr_keyframes(Node node, double blurrRadius, double keyFrame, Timeline timeline) {
        GaussianBlur gb = new GaussianBlur();
        double blurrFadeThreshold = 0.4;
        gb.setRadius(blurrRadius);
        node.setEffect(gb);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(keyFrame), new KeyValue(gb.radiusProperty(), blurrRadius, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(keyFrame + blurrFadeThreshold), new KeyValue(gb.radiusProperty(), 0, Interpolator.EASE_IN))
        );
    }


    public static void nodePopInCard_keyframes(Node node, double endKeyframe, Timeline timeline) {
        node.setOpacity(0);
        node.setScaleX(0);
        node.setScaleY(0);
        double startOffset = 0.5;
        double bounceOffset = startOffset + 0.3;
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleYProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH))
        );
    }

    public static void nodePushOutCard_keyframes(Node node, double endKeyframe, Timeline timeline) {
        node.setOpacity(1);
        node.setScaleX(1);
        node.setScaleY(1);
        double startOffset = 0.5;
        double bounceOffset = startOffset + 0.3;
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleYProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH))
        );
    }

    public static void nodePopIn_keyframes(Node node, Timeline timeline) {
        node.setOpacity(0);
        node.setScaleX(0.5);
        node.setScaleY(0.5);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleXProperty(), 1.1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH))
        );
    }

    public static void nodePushOut_KeyFrames(Node node, Timeline timeline) {
        node.setOpacity(1);
        node.setScaleX(1);
        node.setScaleY(1);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleXProperty(), 0.5, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleYProperty(), 0.5, Interpolator.EASE_BOTH))
        );
    }

    public static void nodeThrowIn_Keyframes(Node node, Timeline timeline) {
        node.setOpacity(0);
        node.setScaleX(1.5);
        node.setScaleY(1.5);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleXProperty(), 0.9, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleYProperty(), 0.9, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH))
        );
    }

    public static void nodeBringOut_KeyFrames(Node node, Timeline timeline) {
        node.setOpacity(1);
        node.setScaleX(1);
        node.setScaleY(1);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.2), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleXProperty(), 1.5, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.3), new KeyValue(node.scaleYProperty(), 1.5, Interpolator.EASE_BOTH))
        );
    }
}
