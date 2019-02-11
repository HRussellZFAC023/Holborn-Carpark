package uk.co.holborn.carparkclient;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;

import static javafx.scene.layout.AnchorPane.*;

/**
 * SceneManager handles the scene changes and animation. It knows the current scene,
 * keeps track of the old scenes stack so going back happens seamless.
 * All the scenes are added on the provided AnchorPane as a parameter.
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 * @see Scenes
 */
public class SceneManager {
    private Logger logger;
    private AnchorPane scenePane;
    private Stack<Scenes> sceneStack;
    private Scenes currentScene;
    private AnchorPane sc;
    private boolean animationFinished;

    /**
     * Constructor
     *
     * @param scenePane to be used for all the scenes
     */
    public SceneManager(AnchorPane scenePane) {
        logger = LogManager.getLogger(getClass().getName());
        this.scenePane = scenePane;
        sceneStack = new Stack<>();
        animationFinished = true;
    }

    /**
     * Change to the provided scene
     * @param scene
     */
    public void changeTo(Scenes scene) {
        if (animationFinished) {
            if (currentScene != null && currentScene != scene) {
                sceneStack.push(currentScene);
            }
            displayScene(scene);
        }
    }

    /**
     * Display the provided scene
     * @param scene to display
     */
    private void displayScene(Scenes scene) {
        displayScene(scene, false);
    }

    /**
     * Display the provided scene
     * @param scene to display
     * @param reversed animation or not
     */
    private void displayScene(Scenes scene, boolean reversed) {
        if (currentScene == null || currentScene != scene) {
            currentScene = scene;
            sc = scene.getScene();
            setBottomAnchor(sc, 0.0);
            setRightAnchor(sc, 0.0);
            setLeftAnchor(sc, 0.0);
            setTopAnchor(sc, 0.0);
            Platform.runLater(() -> {
                try {
                    scenePane.getChildren().add(sc);
                    animateShowIn(reversed);
                } catch (java.lang.IllegalArgumentException e) {
                    logger.error("Tried to add duplicate pane!");
                }

            });
        }
    }

    /**
     * Switch to the previous scene
     */
    public void goBack() {
        if (animationFinished) {
            displayScene(sceneStack.pop(), true);
        }
    }

    /**
     * Display the scenes animations
     * @param reversed animation
     */
    private void animateShowIn(boolean reversed) {
        animationFinished = false;
        Timeline timeline = new Timeline();
        Node oldScene = scenePane.getChildren().get(0);
        if (scenePane.getChildren().size() > 1) {
            Node newScene = scenePane.getChildren().get(1);
            if (reversed) {
                Animator.nodeThrowIn_Keyframes(newScene, timeline);
                Animator.nodePushOut_KeyFrames(oldScene, timeline);
            } else {
                Animator.nodePopIn_keyframes(newScene, timeline);
                Animator.nodeBringOut_KeyFrames(oldScene, timeline);
            }
        } else {
            Animator.nodePopIn_keyframes(oldScene, timeline);
        }
        timeline.setOnFinished(t -> {
            sc = null;
            if (scenePane.getChildren().size() > 1) scenePane.getChildren().remove(0);
            animationFinished = true;
        });
        timeline.play();
    }

    /**
     * Provides the actual scene
     * @return currentScene
     */
    public Scenes getCurrentScene() {
        return currentScene;
    }

    /**
     * Clear scenes stack
     */
    public void clearSceneQueue() {
        sceneStack.clear();
    }
}
