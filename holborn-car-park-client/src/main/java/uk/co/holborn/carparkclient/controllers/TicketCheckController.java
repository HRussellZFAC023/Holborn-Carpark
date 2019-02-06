package uk.co.holborn.carparkclient.controllers;

import com.google.gson.Gson;
import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;
import uk.co.holborn.carparkclient.TicketDetailsPopUp;

import java.net.URL;
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

    private MainViewController mc;
    private Gson gson;
    boolean doScanAnim = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        setMessage("Please insert your ticket");
        Socket socket = mc.getSocket();
        checkTicketField.clear();
        checkTicketField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 36) {
                setMessage("Please wait...");
                validationUI(true);
                socket.emit("fetch-ticket", newValue.substring(0, 36), (Ack) objects -> {
                    Object err = objects[0];
                    Object description = objects[1];
                    if (err.equals(200)) {
                        animateImageValidate(true);
                        setMessage("Your ticket is valid!");
                        gson = new Gson();
                        mc.ticket = gson.fromJson(description.toString(), Ticket.class);
                        TicketDetailsPopUp tp = new TicketDetailsPopUp(mainAnchorPane, blurrAnchorPane);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tp.show(mc.ticket);
                    } else {
                        animateImageValidate(false);
                        setMessage("Invalid ticket! Please seek assistance from a member of staff.");
                    }
                });
            }
        });
        animateImageShow();
    }

    @FXML
    private void goToPayment() {
        mc.sceneManager.switchToScene(Scenes.TICKET_CHECK);
    }

    @FXML
    private void back() {
        mc.sceneManager.goBack();
    }

    private void validationUI(boolean validate){
        Platform.runLater(()->{
            checkTicketField.setDisable(validate);
            checkTicketField.clear();
        });
    }
    private void animateImageShow() {
        Timeline timeline = new Timeline();
        ticket_image_bg.setOpacity(0);
        ticket_image_bg.setTranslateY(-10);
        ticket_image_ticket.setOpacity(0);
        ticket_image_laser_beam.setOpacity(0);
        ticket_image_validated.setOpacity(0);
        ticket_image_validated.setTranslateY(40);
        ticket_image_validated.setScaleX(0);
        ticket_image_validated.setScaleY(0);

        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), new KeyValue(ticket_image_bg.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1), new KeyValue(ticket_image_bg.translateYProperty(), 0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1), new KeyValue(ticket_image_laser_beam.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(ticket_image_laser_beam.opacityProperty(), 1, Interpolator.EASE_OUT))
        );
        timeline.setOnFinished(t -> {
            animateImageScan();
        });
        timeline.play();
    }

    private void animateImageScan() {
        ticket_image_ticket.setTranslateY(40);
        ticket_image_ticket.setOpacity(0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), new KeyValue(ticket_image_ticket.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(1), new KeyValue(ticket_image_ticket.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(2), new KeyValue(ticket_image_ticket.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(2), new KeyValue(ticket_image_ticket.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(3), new KeyValue(ticket_image_ticket.translateYProperty(), 40, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(3), new KeyValue(ticket_image_ticket.opacityProperty(), 0, Interpolator.EASE_BOTH))
        );
        timeline.setOnFinished(t -> {
            if (doScanAnim) animateImageScan();
        });
        timeline.play();


    }

    private void animateImageValidate(boolean valid) {
        String imgURl;
        doScanAnim = false;
        if (valid) {
            imgURl = ("/img/checkmark.png");
        } else {
            imgURl = ("/img/x mark.png");
        }
        ticket_image_validated.setImage(new Image(imgURl));
        ticket_image_validated.setOpacity(0);
        ticket_image_validated.setTranslateY(-50);
        ticket_image_validated.setScaleX(0);
        ticket_image_validated.setScaleY(0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(ticket_image_validated.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(ticket_image_validated.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(ticket_image_validated.scaleYProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(ticket_image_validated.scaleXProperty(), 1, Interpolator.EASE_IN))
        );
        timeline.setOnFinished(t -> {
        });
        timeline.play();
    }

    private void setMessage(String message) {
        Platform.runLater(()->{
            Animator.nodeFade(infoText,false);
            infoText.setText(message);
            Animator.nodeFade(infoText,true);
        });
    }

}
