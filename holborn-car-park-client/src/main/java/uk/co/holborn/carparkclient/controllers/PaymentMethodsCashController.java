package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsCashController implements Initializable {

    @FXML
    Button backButton;
    @FXML
    TextField inputAmount;
    @FXML
    Label price_due;
    @FXML
    Label infoText;
    @FXML
    Label price_paid;
    Ticket t;
    private MainViewController mc;
    private BigDecimal due;
    private BigDecimal paid;
    private BigDecimal change;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
    }

    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    public void setup() {
        t = mc.ticket;
        due = t.getPrice().subtract(t.getAmountPaid());
        paid = t.getAmountPaid();
        change = new BigDecimal("0");
        infoText.setText("");
        backButton.setVisible(true);
        inputAmount.setDisable(false);
        updateUI();
    }

    public void pay() {
        BigDecimal amount;
        try {
            amount = new BigDecimal(inputAmount.getText());
        } catch (Exception e) {
            amount = BigDecimal.ZERO;
        }
        due = due.subtract(amount);
        paid = paid.add(amount);
        t.setAmountPaid(paid);
        if (due.compareTo(BigDecimal.ZERO) <= 0) {
            change = due.abs();
            due = BigDecimal.ZERO;
            inputAmount.setDisable(true);
            backButton.setVisible(false);
            if (change.compareTo(BigDecimal.ZERO) > 0) setInfoText("Please take your change of £" + change);
            Thread t = new Thread(() -> {
                try {
                    mc.emitTicketPaid();
                    Thread.sleep(3000);
                    mc.sceneManager.changeTo(Scenes.LANDING);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.setName("Thread-Sleep");
            t.setDaemon(true);
            t.start();
        }
        inputAmount.clear();
        updateUI();
    }

    private void updateUI() {
        setAmoundDue("£" + due);
        setPaidAmount("£" + paid);
    }

    private void setAmoundDue(String amount) {
        price_due.setText(amount);
        Animator.nodeFade(price_due, true);
    }

    private void setPaidAmount(String amount) {
        price_paid.setText(amount);
        Animator.nodeFade(price_paid, true);
    }

    private void setInfoText(String amount) {
        infoText.setText(amount);
        Animator.nodeFade(price_due, true);
    }


}
