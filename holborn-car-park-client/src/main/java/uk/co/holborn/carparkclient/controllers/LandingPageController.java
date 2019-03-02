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

/**
 * Landing pages provides the default " home screen" for the app. proving paths
 * to all the other scenes and providing the user with useful i
 * information in real time (such as the number of parking spaces and so on)
 *
 * @author Vlad Alboiu
 * @version 1.0.3
 */
public class LandingPageController implements Initializable {

    private MainViewController mc;
    @FXML
    private
    Label welcome;
    @FXML
    private
    Label parking_spaces;
    @FXML
    private
    Label price;
    @FXML
    private
    Label happy_hour;

    /**
     * Method that prepares the ui
     *
     * @param location
     * @param resources
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        Logger logger = LogManager.getLogger(getClass().getName());

        welcome.setText(GlobalVariables.LANDING_PAGE_WELCOME);
        Animator.nodeFade(welcome, true);
    }

    /**
     * This method first asks the server for ui information and then
     * every time a change has been made on the server, it will update it dynamically
     * @since 1.0.2
     */
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
        socket.on("update-carpark-details", objects -> socket.emit("fetch-carpark-details", (Ack) this::update));
    }

    /**
     * Update the information provided by the given object from the socker
     *
     * @param objects received from the socket
     * @since 1.0.2
     */
    private void update(Object[] objects) {
        updateTextParkingSpaces(objects[0] + "");
        updateTextPrice("£" + objects[1]);
        updateHappyHour(((boolean) objects[2] ? "Now" : "Unavailable"));

    }

    /**
     * Method that updates and animates the number of free parking spaces label
     *
     * @param message string to be displayed
     * @since 1.0.2
     */
    private void updateTextParkingSpaces(String message) {
        if (!mc.parking_spaces.equals(message)) {
            mc.parking_spaces = message;
            Platform.runLater(() -> parking_spaces.setText(message));
            Animator.nodeFade(parking_spaces, true);
        }
    }

    /**
     * Method that updates and animates the price
     *
     * @param message string to be displayed
     * @since 1.0.2
     */
    private void updateTextPrice(String message) {
        if (!mc.hourly_price.equals(message)) {
            mc.hourly_price = message;
            Platform.runLater(() -> price.setText(message));
            Animator.nodeFade(price, true);
        }
    }

    /**
     * Method that updates and animates the happy hour state
     *
     * @param message string to be displayed
     * @since 1.0.2
     */
    private void updateHappyHour(String message) {
        if (!mc.happy_hour_time.equals(message)) {
            mc.happy_hour_time = message;
            Platform.runLater(() -> happy_hour.setText(message));
            Animator.nodeFade(happy_hour, true);
        }
    }

    /**
     * Method that changes the scene to the smartcard check.
     * It also starts the session countdown
     *
     * @since 1.0.3
     */
    @FXML
    public void smartcardCheck() {
        mc.sceneManager.changeTo(Scenes.SMARTCARD_CHECK);
        mc.startSession();
    }

    /**
     * Method that changes the scene to the ticket check.
     * It also starts the session countdown
     *
     * @since 1.0.0
     */
    @FXML
    public void ticketCheck() {
        mc.sceneManager.changeTo(Scenes.TICKET_CHECK);
        mc.startSession();
    }

}
