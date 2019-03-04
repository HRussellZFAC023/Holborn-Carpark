/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient.controllers;

import com.google.gson.Gson;
import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The check controller handles the interactions of the checking UI.
 * Instead of creating two FXML UI's and controllers,
 * I've reworked the controller to support  both modes.
 *
 * @author Vlad Alboiu
 * @version 1.0.2
 */
public class CheckController implements Initializable {

    private String VALID_MESSAGE;
    private String INVALID_MESSAGE;
    private String DOESNT_EXIST;
    private String SOCKET_EMIT;
    private String INFO;

    @FXML
    private
    TextField checkTicketField;
    @FXML
    private
    Label infoText;
    @FXML
    private
    ImageView ticket_image;
    @FXML
    private
    ImageView ticket_image_validated;
    @FXML
    private
    AnchorPane mainAnchorPane;
    @FXML
    private
    AnchorPane blurrAnchorPane;
    @FXML
    private
    Button backButton;
    private final Socket socket;
    private TicketDetailsPopUp tp;
    private final Logger logger;
    private final MainViewController mc;
    private final Gson gson;
    private Sprite sprite;
    private boolean isTicketFromSmartCard;

    /**
     * The constructor initialises the logger and gets the necessary
     * variables from the main view controller
     *
     * @since 1.0.0
     */
    public CheckController() {
        logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
        socket = mc.getSocket();
        gson = new Gson();
    }


    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sprite = new Sprite(ticket_image, mc.getSpriteSheets().getSpriteSettings(Sprites.TICKET_INSERT));
        ticket_image.setCache(true);
        ticket_image.setCacheHint(CacheHint.SPEED);
        tp = new TicketDetailsPopUp(mainAnchorPane, blurrAnchorPane);
        checkTicketField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 36) {
                setMessage("Please wait...");
                animateTicketUIHide();
                validationUI(true);
                String req = newValue.substring(0, 36).toLowerCase();
                socket.emit(SOCKET_EMIT, req, (Ack) objects -> {
                    Object err = objects[0];
                    Object description = objects[1];
                    logger.info(err + " " + description);
                    if (err.equals(200)) {
                        mc.ticket = gson.fromJson(objects[2].toString(), Ticket.class);
                        if (mc.ticket.isValid()) {
                            if (!mc.ticket.isPaid()) {
                                animateImageValidate(true);
                                setMessage(VALID_MESSAGE);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                tp.show(mc.ticket);
                            } else {
                                mc.ticket.setReceivedFromSmartcard(isTicketFromSmartCard);
                                mc.ticket.setPaidOnReceived(true);
                                mc.sceneManager.changeTo(Scenes.FINISH);
                            }
                        } else {
                            setInvalidUI(INVALID_MESSAGE);
                        }
                    } else {
                        setInvalidUI(DOESNT_EXIST);
                    }
                });
            }
        });

    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.1
     */
    private void setup() {
        resetAnimPoses();
        validationUI(false);
        setMessage(INFO);
        checkTicketField.clear();
        tp.remove();
        animateImageShow();
    }

    /**
     * Prepares the check mode for  tickets
     *
     * @since 1.0.2
     */
    public void setTicketMode() {
        sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(Sprites.TICKET_INSERT));
        INFO = "Please insert your ticket";
        SOCKET_EMIT = "fetch-ticket";
        VALID_MESSAGE = "Your ticket is valid!";
        INVALID_MESSAGE = "The ticket is invalid! Please seek assistance from a member of staff.";
        DOESNT_EXIST = "Your ticket could not be found! Please seek assistance from a member of staff.";
        isTicketFromSmartCard = false;
        setup();
    }

    /**
     * Prepares the check mode for smart cards
     *
     * @since 1.0.2
     */
    public void setSmartcardMode() {
        sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(Sprites.SMARTCARD_CHECK));
        INFO = "Please bring your smart card near the reader";
        SOCKET_EMIT = "fetch-smartcard";
        VALID_MESSAGE = "Your smart card is valid";
        INVALID_MESSAGE = "The smart card session is invalid! Please seek assistance from a member of staff.";
        DOESNT_EXIST = "The smart card could not be found! Please seek assistance from a member of staff.";
        isTicketFromSmartCard = true;
        setup();
    }


    /**
     * Go back to the previous scene
     *
     * @since 1.0.0
     */
    @FXML
    private void back() {
        mc.sceneManager.goBack();
    }

    /**
     * Set the validation ui, displaying the text field and back button
     *
     * @param show if to show the ui or not
     * @since 1.0.1
     */
    private void validationUI(boolean show) {
        backButton.setVisible(!show);
        checkTicketField.setDisable(show);
        if (show) {
            Animator.nodePushOut(checkTicketField);
        } else Animator.nodePopIn(checkTicketField, 0.2);
    }

    /**
     * Set the invalid UI, displaying a message
     * and displaying the red X image
     *
     * @param text the message to be shown
     * @since 1.0.1
     */
    private void setInvalidUI(String text) {
        animateImageValidate(false);
        setMessage(text);
        Platform.runLater(() -> backButton.setVisible(true));
    }

    /**
     * Animate the image, giving it a pop effect
     *
     * @since 1.0.1
     */
    private void animateImageShow() {
        Animator.nodePopIn(ticket_image, 0.6, e -> sprite.replay());
    }

    /**
     * Animate hiding the sprite
     *
     * @since 1.0.1
     */
    private void animateTicketUIHide() {
        sprite.pause();
        Animator.nodePushOut(ticket_image);
    }

    /**
     * Reset animations poses
     *
     * @since 1.0.1
     */
    private void resetAnimPoses() {
        sprite.resetView();
        ticket_image_validated.setOpacity(0);
        ticket_image_validated.setTranslateY(0);
        ticket_image_validated.setScaleX(0);
        ticket_image_validated.setScaleY(0);
    }

    /**
     * Animate image validation image (pop in and displaying either the red X or green check mark image)
     *
     * @param valid
     * @since 1.0.1
     */
    private void animateImageValidate(boolean valid) {
        ticket_image_validated.setImage(new Image(valid ? "/img/checkmark.png" : "/img/x mark.png"));
        Animator.nodePopIn(ticket_image_validated);
    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.1
     */
    private void setMessage(String message) {
        Platform.runLater(() -> infoText.setText(message));
        Animator.nodeFade(infoText, true);
    }

}
