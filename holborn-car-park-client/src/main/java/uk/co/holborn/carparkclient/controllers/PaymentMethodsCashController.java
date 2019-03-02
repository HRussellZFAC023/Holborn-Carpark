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

/**
 * The payment cash controller handles the interaction of the cash payment screen
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class PaymentMethodsCashController implements Initializable {

    @FXML
    private
    Button backButton;
    @FXML
    private
    TextField inputAmount;
    @FXML
    private
    ImageView imageView;
    @FXML
    private
    Label price_due;
    @FXML
    private
    Label price_paid;
    private Ticket t;
    private MainViewController mc;
    private BigDecimal due;
    private BigDecimal paid;
    private BigDecimal change;
    private Sprite sprite;

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     * In here we change the theme to default one (Which is light
     *
     * @param location
     * @param resources
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(Sprites.COINS_IN));
    }

    /**
     * Go back to the previous scene
     *
     * @since 1.0.0
     */
    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     *
     * @since 1.0.1
     */
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

    /**
     * Method that calculates the amounts and updates the screen
     *
     * @since 1.0.1
     */
    @FXML
    void pay() {
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

    /**
     * Update the ui text
     *
     * @since 1.0.1
     */
    private void updateUI() {
        setAmoundDue("£" + due);
        setPaidAmount("£" + paid);
    }

    /**
     * Animate the amount due label and update its text
     *
     * @param amount the amount to update the label with
     * @since 1.0.1
     */
    private void setAmoundDue(String amount) {
        price_due.setText(amount);
        Animator.nodeFade(price_due, true);
    }

    /**
     * Animate the paid amount label and update its text
     *
     * @param amount the amount to update the label with
     * @since 1.0.1
     */
    private void setPaidAmount(String amount) {
        price_paid.setText(amount);
        Animator.nodeFade(price_paid, true);
    }


}
