/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package FxStuff.Controllers;

import FxStuff.Animator;
import FxStuff.GlobalVariables;
import FxStuff.Scenes;
import FxStuff.Sprites.Sprite;
import FxStuff.Sprites.Sprites;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The finish controller handles the interactions of the finish UI.
 * The scene thanks the user after their ticket/smartcard has been validated
 * This controller has a countdown of a specified amount in the global variables class. After that countdown,
 * the scene will automatically switch back to the landing page.
 *
 * @author Vlad Alboiu
 * @author Cameron
 * @version 1.0.1
 */
public class FinishController implements Initializable {
    @FXML
    private
    Label infoText;
    @FXML
    private
    Label aditionalInfo;
    @FXML
    private
    ImageView ticket_image;
    private final MainViewController mc;
    private Sprite sprite;

    /**
     * Constructor
     */
    public FinishController() {
        Logger logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
    }

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sprite = new Sprite(ticket_image, mc.getSpriteSheets().getSpriteSettings(Sprites.THANK_YOU));
        ticket_image.setCache(true);
        ticket_image.setCacheHint(CacheHint.SPEED);

    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     */
    public void setup() {
        sprite.resetView();
        setMessage();
        animateImageShow();
    }

    /**
     * This method called by the next customer button will change to the landing page
     */
    @FXML
    private void nextCustomer() {
        mc.getSceneManager().reverseTo(GlobalVariables.getBarrierType()? Scenes.LANDING_IN:Scenes.LANDING_OUT);
    }

    /**
     * Animate image showing in
     */
    private void animateImageShow() {
        Animator.nodePopIn(ticket_image, 0.6, e -> sprite.replay(1));
    }

    /**
     * Inform the user with a message
     */
    private void setMessage() {
        infoText.setText("Thank you for visiting us! Have a safe journey!");
        Animator.nodeFade(infoText, true);
    }
}
