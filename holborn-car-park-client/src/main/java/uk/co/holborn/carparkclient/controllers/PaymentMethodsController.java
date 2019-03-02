package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * The check controller handles the interactions of the payment methods UI.
 *
 * @author Vlad Alboiu
 * @version 1.0.2
 */
public class PaymentMethodsController implements Initializable {

    @FXML
    Button backButton;
    @FXML
    private
    Label infoText;
    @FXML
    private
    Label amountText;
    @FXML
    private
    Label price;
    private MainViewController mc;

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();

    }

    /**
     * Switch to cash payment screen
     * @since 1.0.0
     */
    @FXML
    private void cashPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS_CASH);
    }

    /**
     * Switch to cclass ard payment screen
     * @since 1.0.2
     */
    @FXML
    private void cardPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS_CLASSIC_CARD);
    }
    /**
     * Switch to compactness payment screen
     * @since 1.0.1
     */
    @FXML
    private void contactlessPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS_CONTACTLESS);
    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.0
     */
    public void setup() {
        if (mc.ticket.getAmountInTicketMachine().compareTo(BigDecimal.ZERO) > 0) {
            infoText.setText("Please pay the remaining amount");
            amountText.setText("Amount remaining");
        } else {
            infoText.setText("Please choose your payment method");
            amountText.setText("Fee");
        }
        price.setText("£" + (mc.ticket.getPrice().subtract(mc.ticket.getAmountInTicketMachine())));
    }

    /**
     * Go back to the previous scene
     *
     * @since 1.0.0
     */
    @FXML
    private void back() {
        mc.sceneManager.changeTo(Scenes.FINISH);
    }


}
