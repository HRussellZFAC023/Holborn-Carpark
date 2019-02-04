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
        welcome.setText(GlobalVariables.landing_page_welcome);
        Animator.nodeFade(welcome, true);

        Socket socket = mc.getSocket();
        socket.emit("fetch-carpark-details", (Ack) objects -> {
            Platform.runLater(()->{
                updateText(parking_spaces,objects[0] +"");
                updateText(price, "£"+ objects[1]);
            });
        });
        socket.on("updated-carpark-details", objects -> {
            Platform.runLater(()->{
                updateText(parking_spaces,objects[0] +"");
                updateText(price, "£"+ objects[1]);
            });
        });
    }

    private void updateText(Label label, String message){
        Animator.nodeFade(label, true);
        label.setText(message);
    }
    @FXML
    public void begin() {
        mc.sceneManager.switchToScene("TicketCheck");
    }

}
