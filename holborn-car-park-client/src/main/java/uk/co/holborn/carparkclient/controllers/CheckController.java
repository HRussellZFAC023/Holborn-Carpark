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

public class CheckController implements Initializable {

    String VALID_MESSAGE;
    String INVALID_MESSAGE;
    String SOCKET_EMIT;
    String INFO;

    @FXML
    TextField checkTicketField;
    @FXML
    Label infoText;
    @FXML
    ImageView ticket_image;
    @FXML
    ImageView ticket_image_validated;
    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    AnchorPane blurrAnchorPane;
    @FXML
    Button backButton;
    Socket socket;
    TicketDetailsPopUp tp;
    private Logger logger;
    private MainViewController mc;
    private Gson gson;
    Sprite sprite;

    public CheckController() {
        logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
        socket = mc.getSocket();
        gson = new Gson();
    }


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
                socket.emit(SOCKET_EMIT, newValue.substring(0, 36), (Ack) objects -> {
                    Object err = objects[0];
                    Object description = objects[1];
                    logger.info(err + " " + description);
                    if (err.equals(200)) {
                        animateImageValidate(true);
                        setMessage(VALID_MESSAGE);
                        mc.ticket = gson.fromJson(objects[2].toString(), Ticket.class);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tp.show(mc.ticket);
                    } else {
                        animateImageValidate(false);
                        setMessage(INVALID_MESSAGE);
                        Platform.runLater(() -> backButton.setVisible(true));

                    }
                });
            }
        });

    }

    private void setup() {
        resetAnimPoses();
        validationUI(false);
        setMessage(INFO);
        checkTicketField.clear();
        tp.remove();
        animateImageShow();
    }

    public void setTicketMode(){
        sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(Sprites.TICKET_INSERT));
        INFO = "Please insert your ticket";
        SOCKET_EMIT = "fetch-ticket";
        VALID_MESSAGE = "Your ticket is valid!";
        INVALID_MESSAGE = "The ticket is invalid! Please seek assistance from a member of staff.";
        setup();
    }
    public void setSmartcardMode(){
        sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(Sprites.SMARTCARD_CHECK));
        INFO = "Please bring your smart card near the reader";
        SOCKET_EMIT = "fetch-smartcard";
        VALID_MESSAGE = "Your smart card is valid";
        INVALID_MESSAGE = "The smart card is invalid! Please seek assistance from a member of staff.";
        setup();
    }
    @FXML
    private void goToPayment() {
        mc.sceneManager.changeTo(Scenes.TICKET_CHECK);
    }

    @FXML
    private void back() {
        mc.sceneManager.goBack();
    }

    private void validationUI(boolean show) {
        backButton.setVisible(!show);
        checkTicketField.setDisable(show);
        if (show) {
            Animator.nodePushOut(checkTicketField);
        } else Animator.nodePopIn(checkTicketField, 0.2);
    }

    private void animateImageShow() {
        Animator.nodePopIn(ticket_image, 0.6, e -> {
            sprite.replay();
        });
    }

    private void animateTicketUIHide() {
        sprite.pause();
        Animator.nodePushOut(ticket_image);
    }

    private void resetAnimPoses() {
        sprite.resetView();
        ticket_image_validated.setOpacity(0);
        ticket_image_validated.setTranslateY(0);
        ticket_image_validated.setScaleX(0);
        ticket_image_validated.setScaleY(0);
    }

    private void animateImageValidate(boolean valid) {
        ticket_image_validated.setImage(new Image(valid ? "/img/checkmark.png" : "/img/x mark.png"));
        Animator.nodePopIn(ticket_image_validated);
    }

    private void setMessage(String message) {
        Platform.runLater(() -> {
            infoText.setText(message);
        });
        Animator.nodeFade(infoText, true);
    }

}
