package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsController implements Initializable {

    @FXML
    Button backButton;
    @FXML
    Label infoText;
    @FXML
    Label amountText;
    @FXML
    Label price;
    private MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();

    }

    @FXML
    private void cashPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS_CASH);
    }

    @FXML
    private void cardPayment() {

    }

    @FXML
    private void contactlessPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS_CONTACTLESS);
    }

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

    @FXML
    private void back() {
        mc.sceneManager.changeTo(Scenes.FINISH);
    }


}
