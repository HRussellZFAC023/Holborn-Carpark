/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu. All rights reserved.
 */

package uk.co.holborn.carparkclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Sprite;
import uk.co.holborn.carparkclient.Sprites;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The payment contactless controller handles the interaction of the contatcless payment screen
 *
 * @author Vlad Alboiu
 * @version 1.0.0
 */
public class PaymentMethodsClassicCardController implements Initializable {
    @FXML
    private
    ImageView imageView;
    @FXML
    private
    Label infoText;
    @FXML
    private
    ImageView imageValidate;
    @FXML
    private
    Button backButton;
    @FXML
    private
    Label amountText;
    @FXML
    private
    Label price;

    private MainViewController mc;
    private Sprite sprite;
    private Thread transactionThread;

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     * In here we change the theme to default one (Which is light
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(Sprites.PAYMENT_CARD_CLASSIC));
    }

    /**
     * Go back to the previous scene
     *
     * @since 1.0.0
     */
    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    /**
     * Configure the toggles
     *
     * @since 1.0.0
     */
    private void configureToggles() {

    }


    /**
     * Simulate a transaction happening (since there's no hardware we can test this)
     *
     * @param approved whether or not to approve the transaction
     * @since 1.0.0
     */
    private void fakeTransaction(boolean approved) {
        if (transactionThread != null) transactionThread.interrupt();
        transactionThread = new Thread(() -> {
            try {
                imageValidate.setOpacity(0);
                setMessage("Please wait...");
                setCheckMode(true);
                spritePushOut();
                Thread.sleep(2000);
                if (approved) {
                    animateImageValidate(true);
                    setMessage("Transaction approved!");
                } else {
                    animateImageValidate(false);
                    setMessage("Transaction refused! Please try again or choose another payment method");
                    setCheckMode(false);
                }
                Thread.sleep(1000);
                if (approved) {
                    mc.ticket.setPaid(true);
                    mc.sceneManager.changeTo(Scenes.FINISH);
                }
            } catch (InterruptedException ignored) {

            }
        });
        transactionThread.setName("Thread - Transaction Thread");
        transactionThread.setDaemon(true);
        transactionThread.start();
    }

    /**
     * Approve the transaction button
     *
     * @since 1.0.0
     */
    @FXML
    private void transactionApproved() {
        fakeTransaction(true);

    }

    /**
     * Refuse the transaction button
     *
     * @since 1.0.0
     */
    @FXML
    private void transactionRefused() {
        fakeTransaction(false);
    }

    /**
     * Push the sprite out, hiding it off the screen
     *
     * @since 1.0.0
     */
    private void spritePushOut() {
        sprite.pause();
        Animator.nodePushOut(imageView);
    }

    /**
     * Check mode reefers to when the transaction is being verified for approval
     *
     * @param checkMode true or false
     * @since 1.0.0
     */
    private void setCheckMode(boolean checkMode) {
        backButton.setVisible(!checkMode);
    }

    /**
     * Set the validation image
     *
     * @param valid true or false
     * @since 1.0.0
     */
    private void animateImageValidate(boolean valid) {
        imageValidate.setImage(new Image(valid ? "/img/checkmark.png" : "/img/x mark.png"));
        Animator.nodePopIn(imageValidate);
    }



    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.0
     */
    public void setup() {
        imageValidate.setOpacity(0);
        setMessage("Please insert your credit/debit card and follow the terminal prompts");
        configureToggles();
        setCheckMode(false);
        sprite.resetView();
        Animator.nodePopIn(imageView, 0.6, e-> sprite.replay());

        if (mc.ticket.getAmountInTicketMachine().compareTo(BigDecimal.ZERO) > 0) {
            amountText.setText("Amount remaining");
        } else {
            amountText.setText("Fee");
        }
        price.setText("£" + (mc.ticket.getPrice().subtract(mc.ticket.getAmountInTicketMachine())));
    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.0
     */
    private void setMessage(String message) {
        Platform.runLater(() -> infoText.setText(message));
        Animator.nodeFade(infoText, true);
    }

}
