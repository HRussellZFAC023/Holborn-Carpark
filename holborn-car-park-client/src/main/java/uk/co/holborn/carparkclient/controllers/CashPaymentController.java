package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;

import java.net.URL;
import java.util.ResourceBundle;

public class CashPaymentController implements Initializable {

    @FXML
    Label amountPaid;
    @FXML
    Label amountDue;
    private MainViewController mc;
    private double due = 6.50;
    private double paid = 0.00;
    private double change = 0.00;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        updateFields();
    }

    @FXML
    public void add1Pound() {
        addValue(1.0);
    }

    @FXML
    public void add50Pence() {
        addValue(0.50);
    }

    private void addValue(double amount) {
        paid += amount;
        due -= amount;
        updateFields();
        verifyPayment();
    }

    private void verifyPayment() {
        if (due <= 0) {
            change = Math.abs(due);
            System.out.println("Here's your change: " + change);
            mc.sceneManager.changeTo(Scenes.LANDING);
        }
    }

    private void updateFields() {
        amountPaid.setText("£" + paid);
        amountDue.setText("£" + due);
    }


}
