package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class CheckTicketController implements Initializable {


    @FXML
    TextField checkTicketField;
    @FXML
    Label infoText;
    @FXML
    Label timeLabel;
    @FXML
    Label priceLabel;
    @FXML
    GridPane ticketInfoPane;
    @FXML
    Button payButton;
    @FXML
    ImageView ticket_image_bg;
    @FXML
    ImageView ticket_image_ticket;
    @FXML
    ImageView ticket_image_laser_beam;

    private MainViewController mc;
    boolean doScanAnim = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        Socket socket = mc.getSocket();
        checkTicketField.clear();
        checkTicketField.textProperty().addListener((observable, oldValue, newValue)->{
            if(newValue.length() == 24){
                socket.emit("fetch-ticket", newValue, (Ack) objects -> {
                    System.out.println(objects[0]);
                    System.out.println(objects[1]);
                });
                if(newValue.equals("5c4e4332d6a2be0a91f3872d")){
                    displayInfo("Searching...");
                    displayTicketPane("12 minutes", "£6.20");

                }else{
                    displayInfo("Invalid ticket");
                }
            }else{
                setInfoTextVisible(false);
            }
        });
        setInfoTextVisible(false);
        setTicketInfoPaneVisible(false);
        animateImageShow();
    }

    @FXML
    private void goToPayment(){
        mc.sceneManager.switchToScene("PaymentMethod");
    }
    @FXML
    private void back(){
        mc.sceneManager.goBack();
    }

    private void animateImageShow(){
        Timeline timeline = new Timeline();
        ticket_image_bg.setOpacity(0);
        ticket_image_bg.setTranslateY(-10);
        ticket_image_ticket.setOpacity(0);
        ticket_image_laser_beam.setOpacity(0);

        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1),new KeyValue(ticket_image_bg.opacityProperty(), 1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1),new KeyValue(ticket_image_bg.translateYProperty(), 0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1),new KeyValue(ticket_image_laser_beam.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(1.2),new KeyValue(ticket_image_laser_beam.opacityProperty(), 1, Interpolator.EASE_OUT))
        );
        timeline.setOnFinished(t -> {
            animateImageScan();
        });
        timeline.play();
    }
    private void animateImageScan(){
        ticket_image_ticket.setTranslateY(40);
        ticket_image_ticket.setOpacity(0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1),new KeyValue(ticket_image_ticket.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(1),new KeyValue(ticket_image_ticket.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(2),new KeyValue(ticket_image_ticket.opacityProperty(), 1, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(2),new KeyValue(ticket_image_ticket.translateYProperty(), 0, Interpolator.EASE_IN)),
                new KeyFrame(Duration.seconds(3),new KeyValue(ticket_image_ticket.translateYProperty(), 40, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(3),new KeyValue(ticket_image_ticket.opacityProperty(), 0, Interpolator.EASE_BOTH))
        );
        timeline.setOnFinished(t -> {
           if(doScanAnim)  animateImageScan();
        });
        timeline.play();


    }
    private void setInfoTextVisible(boolean visible){
        infoText.setVisible(visible);
    }
    private void setTicketInfoPaneVisible(boolean visible){
        ticketInfoPane.setVisible(visible);
        payButton.setVisible(visible);
    }
    private void displayTicketPane(String time, String price){
        setInfoTextVisible(false);
        timeLabel.setText(time);
        priceLabel.setText(price);
        setTicketInfoPaneVisible(true);

    }
    private void displayInfo(String message){
        setInfoTextVisible(true);
        infoText.setText(message);
    }


}
