package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import uk.co.holborn.carparkclient.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsCashController implements Initializable {

    @FXML
    Button backButton;
    @FXML
    TextField inputAmount;
    @FXML
    ImageView imageView;
    @FXML
    Label price_due;
    @FXML
    Label price_paid;
    Ticket t;
    private MainViewController mc;
    private BigDecimal due;
    private BigDecimal paid;
    private BigDecimal change;
    private Sprite sprite;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(Sprites.COINS_IN));
    }

    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    public void setup() {
        t = mc.ticket;
        sprite.replay();
        due = t.getPrice().subtract(t.getAmountInTicketMachine());
        paid = t.getAmountInTicketMachine();
        change = new BigDecimal("0");
        inputAmount.clear();
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
        t.setAmountInTicketMachine(paid);
        if (due.compareTo(BigDecimal.ZERO) <= 0) {
            change = due.abs();
            due = BigDecimal.ZERO;
            inputAmount.setDisable(true);
            backButton.setVisible(false);
            t.setChange(change);
            t.setPaid(true);
            mc.sceneManager.changeTo(Scenes.FINISH);
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



}
