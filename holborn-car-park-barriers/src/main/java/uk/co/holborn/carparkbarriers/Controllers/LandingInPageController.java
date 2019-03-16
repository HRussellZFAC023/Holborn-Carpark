package uk.co.holborn.carparkbarriers.Controllers;

import uk.co.holborn.carparkbarriers.Animator;
import uk.co.holborn.carparkbarriers.GlobalVariables;
import uk.co.holborn.carparkbarriers.Scenes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Landing_in page provides the default "home screen" for the in barrier app. Providing paths
 * to all the other relevant scenes and providing the user with useful
 * information(such as the number of parking spaces and so on)
 *
 * @author Vlad Alboiu
 * @author Cameron
 * @version 1.0.3
 */
public class LandingInPageController implements Initializable {

    private MainViewController mainCont;

    @FXML
    Label welcome;
    @FXML
    Label parking_spaces;
    @FXML
    Label price;
    @FXML
    Label happy_hour;


    /**
     * Constructor that passes in the mainViewController for referencing
     *
     * @param mainCont The mainViewController
     * @since 1.0.0
     */
    public LandingInPageController(MainViewController mainCont){
        Logger logger = LogManager.getLogger(getClass().getName());
        this.mainCont = mainCont;
    }

    /**
     * Method that prepares the ui
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcome.setText(GlobalVariables.LANDING_PAGE_WELCOME);
        Animator.nodeFade(welcome, true);
    }

    /**
     * This method first asks the server for ui information
     * @since 1.0.2
     */
    public void fetchInformation(){
        String fetching = "Fetching...";
        if (mainCont.parking_spaces.isEmpty()) updateTextParkingSpaces(fetching);
        else parking_spaces.setText(mainCont.parking_spaces);
        if (mainCont.hourly_price.isEmpty()) updateTextPrice(fetching);
        else price.setText(mainCont.hourly_price);
        if (mainCont.happy_hour.isEmpty()) updateHappyHour(fetching);
        else happy_hour.setText(mainCont.happy_hour);
        update(mainCont.getCarparkDetails());
    }

    /**
     * Update the information with the data from the socket
     *
     * @param data received from the socket
     * @since 1.0.2
     */
    private void update(String[] data) {
        Platform.runLater(() -> {
            System.out.println("Updating");
            updateTextParkingSpaces(data[0]);
            updateTextPrice(data[1]);
            updateHappyHour(data[2]);
        });
    }

    /**
     * Method that updates and animates the number of free parking spaces label
     *
     * @param message string to be displayed
     * @since 1.0.2
     */
    private void updateTextParkingSpaces(String message) {
        if (!mainCont.parking_spaces.equals(message)) {
            mainCont.parking_spaces = message;
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
        if (!mainCont.hourly_price.equals(message)) {
            mainCont.hourly_price = message;
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
        if (!mainCont.happy_hour.equals(message)) {
            mainCont.happy_hour = message;
            Platform.runLater(() -> happy_hour.setText(message));
            Animator.nodeFade(happy_hour, true);
        }
    }

    /**
     * Method that changes the scene to the ticket printing screen.
     *
     * @since 1.0.0
     */
    @FXML
    public void begin() {
        mainCont.getSceneManager().changeTo(Scenes.PRINT_TICKET);
        update(mainCont.getCarparkDetails());
    }
}
