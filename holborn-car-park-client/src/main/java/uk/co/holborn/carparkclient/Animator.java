package uk.co.holborn.carparkclient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

import java.util.List;

/**
 * Animator class implements all the animations used during runtime
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class Animator {
    public static void nodeFade(Node node, boolean in, double keyFrameTime) {
        double opacityEnd, opacityStart;
        Timeline timeline = new Timeline();
        double fadeInPopOffset = 0.2;
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
                    new KeyFrame(Duration.seconds(keyFrameTime - fadeInPopOffset), new KeyValue(node.scaleXProperty(), 1.1, Interpolator.EASE_IN)),
                    new KeyFrame(Duration.seconds(keyFrameTime - fadeInPopOffset), new KeyValue(node.scaleYProperty(), 1.1, Interpolator.EASE_IN)),
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
        gaussian_blurr_keyframes(node, blurrRadius, keyFrameTime, timeline);
        timeline.play();

    }

    public static void nodeBlurrBackgroundAndShowPopUp(Node background, Node popup, EventHandler<ActionEvent> eventEventHandler) {
        Timeline timeline = new Timeline();
        double startKey = 0.2;
        nodePopInCard_keyframes(popup, startKey, timeline);
        gaussian_blurr_keyframes(background, 30, startKey + 0.4, startKey + 0.6, timeline);
        timeline.setOnFinished(eventEventHandler);
        timeline.play();
    }

    public static void nodeReverseBlurrBackgroundAndHidePopup(Node background, Node popup, EventHandler<ActionEvent> eventEventHandler) {
        Timeline timeline = new Timeline();
        double blurrEndKey = 0.4;
        nodePushOutCard_keyframes(popup, blurrEndKey, timeline);
        reverseBlurr_keyframes(background, 60, blurrEndKey + 0.3, timeline);
        timeline.setOnFinished(eventEventHandler);
        timeline.play();
    }

    public static void gaussian_blurr_keyframes(Node node, double blurrRadius, double keyFrame, Timeline timeline) {
        gaussian_blurr_keyframes(node, blurrRadius, 0, keyFrame, timeline);
    }

    public static void gaussian_blurr_keyframes(Node node, double blurrRadius, double start_keyframe, double end_keyframe, Timeline timeline) {
        GaussianBlur gb = new GaussianBlur();
        gb.setRadius(0);
        node.setEffect(gb);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(start_keyframe), new KeyValue(gb.radiusProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(end_keyframe), new KeyValue(gb.radiusProperty(), blurrRadius, Interpolator.EASE_BOTH))
        );
    }

    public static void box_blurr_keyframes(Node node, double iterations, double keyFrame, Timeline timeline) {
        BoxBlur bb = new BoxBlur();
        bb.setWidth(10);
        bb.setHeight(10);
        bb.setIterations(0);
        node.setEffect(bb);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(keyFrame), new KeyValue(bb.iterationsProperty(), iterations, Interpolator.EASE_IN))
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
        node.setTranslateY(-500);
        double startOffset = 0.2;
        double bounceOffset = startOffset + 0.2;
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleXProperty(), 0.5, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleYProperty(), 0.5, Interpolator.EASE_BOTH)),

                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleYProperty(), 1.2, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.translateYProperty(), 0, Interpolator.EASE_BOTH)),

                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + bounceOffset), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH))
        );
    }

    public static void nodePushOutCard_keyframes(Node node, double endKeyframe, Timeline timeline) {
        node.setOpacity(1);
        node.setScaleX(1);
        node.setScaleY(1);
        double startOffset = 0.3;
        double bounceOffset = startOffset + 0.3;
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleXProperty(), 1.1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe), new KeyValue(node.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleXProperty(), 0.5, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.scaleYProperty(), 0.5, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(endKeyframe + startOffset), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH))
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

    public static void nodePushOut(Node node) {
        Timeline timeline = new Timeline();
        node.setOpacity(1);
        node.setScaleX(1);
        node.setScaleY(1);
        nodePushOut_KeyFrames(node, timeline);
        timeline.play();
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

    public static void nodePopIn(Node node) {
        nodePopIn(node, 0.2, null);
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

    public static void nodePopIn(Node node, double startframe){
        nodePopIn(node, 0.2, null);
    }
    public static void nodePopIn(Node node, double startframe, EventHandler<ActionEvent> eventHandler) {
        Timeline timeline = new Timeline();
        node.setOpacity(0);
        node.setScaleX(0);
        node.setScaleY(0);
        double frameOffset = 0.2;
        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(startframe), new KeyValue(node.opacityProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe), new KeyValue(node.scaleYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe), new KeyValue(node.scaleXProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe + frameOffset), new KeyValue(node.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe + frameOffset), new KeyValue(node.scaleYProperty(), 1.2, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe + frameOffset), new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe + frameOffset + 0.2), new KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startframe + frameOffset + 0.2), new KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_IN))
        );
        timeline.setOnFinished(eventHandler);
        timeline.play();
    }

    public static void nodeTranslateY(Node node, double startY, double endY, double startFrame, double endAfter) {
        Timeline timeline = new Timeline();
        node.setTranslateY(startY);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(startFrame), new KeyValue(node.translateYProperty(), startY, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startFrame + endAfter), new KeyValue(node.translateYProperty(), endY, Interpolator.EASE_IN))
        );
        timeline.play();
    }

    public static void nodeOpacityChange(Node node, double startOp, double endOP, double startFrame, double endAfter) {
        Timeline timeline = new Timeline();
        node.setOpacity(startOp);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(startFrame), new KeyValue(node.opacityProperty(), startOp, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(startFrame + endAfter), new KeyValue(node.opacityProperty(), endOP, Interpolator.EASE_IN))
        );
        timeline.play();
    }

    public static void animation_ticket_check(List<Node> nodes) {
        nodes.get(0).setOpacity(0);
        nodes.get(1).setOpacity(0);
        nodes.get(1).setTranslateY(40);
        nodes.get(2).setOpacity(0);
        nodePopIn(nodes.get(0), 0.5);
        nodePopIn(nodes.get(1), 0.9);
        nodeOpacityChange(nodes.get(2), 0, 1, 0.9, 0.4);
        nodeTranslateY(nodes.get(1), 40, 0, 1.4, 0.9);
    }
}
