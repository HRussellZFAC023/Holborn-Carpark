package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsController implements Initializable {

    private MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();

    }

    @FXML
    private void cashPayment() {

    }

    @FXML
    private void cardPayment() {

    }

    @FXML
    private void contactlessPayment() {

    }

    @FXML
    private void back() {
        mc.sceneManager.goBack();
    }


}
