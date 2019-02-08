package uk.co.holborn.carparkclient.controllers;

import com.google.gson.Gson;
import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;
import uk.co.holborn.carparkclient.TicketDetailsPopUp;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TicketCheckController implements Initializable {


    @FXML
    TextField checkTicketField;
    @FXML
    Label infoText;
    @FXML
    ImageView ticket_image_bg;
    @FXML
    ImageView ticket_image_ticket;
    @FXML
    ImageView ticket_image_laser_beam;
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

    public TicketCheckController() {
        logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
    }

    ArrayList<Node> nodes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        socket = mc.getSocket();
        nodes.add(ticket_image_bg);
        nodes.add(ticket_image_ticket);
        nodes.add(ticket_image_laser_beam);
        tp = new TicketDetailsPopUp(mainAnchorPane, blurrAnchorPane);
        setup();
        checkTicketField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 36) {
                setMessage("Please wait...");
                animateTicketUIHide();
                validationUI(true);
                socket.emit("fetch-ticket", newValue.substring(0, 36), (Ack) objects -> {
                    Object err = objects[0];
                    Object description = objects[1];
                    logger.info(err + " " + description);
                    if (err.equals(200)) {
                        animateImageValidate(true);
                        setMessage("Your ticket is valid!");
                        //  Object ticket = objects[2];
                        gson = new Gson();
                        mc.ticket = gson.fromJson(objects[2].toString(), Ticket.class);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tp.show(mc.ticket);
                    } else {
                        animateImageValidate(false);
                        setMessage("Invalid ticket! Please seek assistance from a member of staff.");
                        Platform.runLater(() -> backButton.setVisible(true));

                    }
                });
            }
        });

    }

    public void setup() {
        resetAnimPoses();
        setMessage("Please insert your ticket");
        validationUI(false);
        checkTicketField.clear();
        tp.remove();
        animateImageShow();
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
        if(show) {
            Animator.nodePushOut(checkTicketField);
        }
        else  Animator.nodePopIn(checkTicketField, 0.2);
    }

    private void animateImageShow() {
        Animator.animation_ticket_check(nodes);
    }

    private void animateTicketUIHide() {
        Animator.nodePushOut(nodes.get(0));
        Animator.nodePushOut(nodes.get(1));
        Animator.nodePushOut(nodes.get(2));
    }

    private void resetAnimPoses() {
        ticket_image_bg.setOpacity(0);
        ticket_image_ticket.setOpacity(0);
        ticket_image_ticket.setTranslateY(40);
        ticket_image_laser_beam.setOpacity(0);
        ticket_image_laser_beam.setScaleX(1);
        ticket_image_laser_beam.setScaleY(1);
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
            Animator.nodeFade(infoText, true);
        });
    }

}
