package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.GlobalVariables;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class LandingPageController implements Initializable {

    private MainViewController mc;
    @FXML
    Label welcome;
    @FXML
    Label parking_spaces;
    @FXML
    Label price;
    @FXML
    Label happy_hour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        String fetching = "Fetching...";
        if(mc.parking_spaces.isEmpty())updateTextParkingSpaces(fetching);
        else parking_spaces.setText(mc.parking_spaces);
        if(mc.hourly_price.isEmpty()) updateTextPrice( fetching);
        else price.setText(mc.hourly_price);
        welcome.setText(GlobalVariables.landing_page_welcome);
        Animator.nodeFade(welcome, true);

        Socket socket = mc.getSocket();
        socket.emit("fetch-carpark-details", (Ack) this::update);
        socket.on("updated-carpark-details", this::update);
    }

    private void update( Object[] objects){
        Platform.runLater(()->{
            updateTextParkingSpaces(objects[0] +"");
            updateTextPrice( "£"+ objects[1]);
        });
    }
    private void updateTextParkingSpaces( String message){
        if(!mc.parking_spaces.equals(message)){
            mc.parking_spaces = message;
            Animator.nodeFade(parking_spaces, true);
            parking_spaces.setText(message);
        }
    }
    private void updateTextPrice( String message){
        if(!mc.hourly_price.equals(message)) {
            mc.hourly_price = message;
            Animator.nodeFade(parking_spaces, true);
            price.setText(message);
        }
    }
    @FXML
    public void begin() {
        mc.sceneManager.switchToScene("TicketCheck");
    }

}
