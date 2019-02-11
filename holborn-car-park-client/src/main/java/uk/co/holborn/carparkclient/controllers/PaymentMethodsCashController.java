package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsCashController implements Initializable {

//    @FXML
//    Label amountPaid;
//    @FXML
//    Label amountDue;
    private MainViewController mc;
    private double due = 6.50;
    private double paid = 0.00;
    private double change = 0.00;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
    }

    @FXML
    public void  back() {
        mc.sceneManager.goBack();
    }




}
