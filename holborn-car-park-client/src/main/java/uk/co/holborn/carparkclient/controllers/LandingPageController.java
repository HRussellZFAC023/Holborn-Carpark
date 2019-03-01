package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.GlobalVariables;
import uk.co.holborn.carparkclient.Scenes;

import java.net.URL;
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
    Logger logger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        logger = LogManager.getLogger(getClass().getName());

        welcome.setText(GlobalVariables.LANDING_PAGE_WELCOME);
        Animator.nodeFade(welcome, true);
    }

    public void enableFetching() {
        String fetching = "Fetching...";
        if (mc.parking_spaces.isEmpty()) updateTextParkingSpaces(fetching);
        else parking_spaces.setText(mc.parking_spaces);
        if (mc.hourly_price.isEmpty()) updateTextPrice(fetching);
        else price.setText(mc.hourly_price);
        if (mc.happy_hour_time.isEmpty()) updateHappyHour(fetching);
        else happy_hour.setText(mc.happy_hour_time);
        Socket socket = mc.getSocket();
        socket.emit("fetch-carpark-details", (Ack) this::update);
        socket.on("update-carpark-details", objects -> {
            socket.emit("fetch-carpark-details", (Ack) this::update);
        });
    }

    private void update(Object[] objects) {
        updateTextParkingSpaces(objects[0] + "");
        updateTextPrice("£" + objects[1]);
        updateHappyHour(((boolean) objects[2] ? "Now" : "Unavailable"));

    }

    private void updateTextParkingSpaces(String message) {
        if (!mc.parking_spaces.equals(message)) {
            mc.parking_spaces = message;
            Platform.runLater(() -> parking_spaces.setText(message));
            Animator.nodeFade(parking_spaces, true);
        }
    }

    private void updateTextPrice(String message) {
        if (!mc.hourly_price.equals(message)) {
            mc.hourly_price = message;
            Platform.runLater(() -> price.setText(message));
            Animator.nodeFade(price, true);
        }
    }

    private void updateHappyHour(String message) {
        if (!mc.happy_hour_time.equals(message)) {
            mc.happy_hour_time = message;
            Platform.runLater(() -> happy_hour.setText(message));
            Animator.nodeFade(happy_hour, true);
        }
    }

    @FXML
    public void smartcardCheck() {
        mc.sceneManager.changeTo(Scenes.SMARTCARD_CHECK);
        mc.startSession();
    }

    @FXML
    public void ticketCheck() {
        mc.sceneManager.changeTo(Scenes.TICKET_CHECK);
        mc.startSession();
    }

}
