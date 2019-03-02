/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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
public class PaymentMethodsContactlessController implements Initializable {
    @FXML
    private
    Label price;
    @FXML
    private
    Label amountText;
    @FXML
    private
    ImageView imageView;
    @FXML
    private
    Label infoText;
    @FXML
    private
    VBox toggleVbox;
    @FXML
    private
    ImageView imageValidate;
    @FXML
    private
    Button backButton;
    private ToggleGroup toggleGroup;

    private MainViewController mc;
    private Sprite sprite;
    private boolean firstTime;
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
        toggleGroup = new ToggleGroup();
    }

    /**
     * Set all the toggles disabled
     *
     * @since 1.0.0
     */
    private void setTogglesDisabled() {
        for (Toggle tg : toggleGroup.getToggles()) {
            if (tg.isSelected()) ((ToggleButton) tg).setDisable(true);
            else ((ToggleButton) tg).setDisable(false);
        }
    }

    /**
     * Show the sprite and the update it accordingly
     *
     * @param sp the sprite for the image to be updated with
     * @since 1.0.0
     */
    private void popInSprite(Sprites sp) {
        if (sprite == null) sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(sp));
        else {
            sprite.pause();
            sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(sp));
        }
        if (firstTime) {
            firstTime = false;
            Animator.nodePopIn(imageView, 0.4, e -> sprite.replay());
        } else {
            sprite.replay();
            Animator.nodePopIn(imageView, 0.0, e -> {
            });
        }


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
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(0));
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(1));
        toggleGroup.getToggles().get(0).setUserData("Debit/Credit");
        toggleGroup.getToggles().get(1).setUserData("Apple Pay");
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                imageValidate.setOpacity(0);
                if (toggleGroup.getSelectedToggle().getUserData().equals("Apple Pay")) {
                    popInSprite(Sprites.PAYMENT_APPLE_PAY);
                    setMessage("Please bring your device near the reader");
                } else {
                    popInSprite(Sprites.PAYMENT_CARD_CONTACTLESS);
                    setMessage("Please bring your debit/credit card near the reader");
                }
                setTogglesDisabled();
            }
        });
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
        toggleVbox.setDisable(checkMode);
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
        firstTime = true;
        imageValidate.setOpacity(0);
        toggleVbox.setDisable(false);
        configureToggles();
        setCheckMode(false);
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));

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
