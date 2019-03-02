package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * The check controller handles the interactions of the ticket details UI.
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class TicketDetailsPopUpController implements Initializable {

    @FXML
    private
    Label date_in;
    @FXML
    private
    Label date_check_out;
    @FXML
    private
    Label price;
    @FXML
    private
    Label duration;
    @FXML
    private
    Label duration_paying_for;
    @FXML
    private
    Label discount;
    @FXML
    private
    Label discountFrom;
    @FXML
    private
    Label discountSave;
    @FXML
    private
    Button payButton;
    private MainViewController mc;
    private Scenes location;

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
    }

    /**
     * Method that updates the screen values from the ticket
     *
     * @param ticket the ticket to update the on screen label values with
     * @since 1.0.0
     */
    public void setTicket(Ticket ticket) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm d/MM/yyyy");
        date_in.setText(dateFormat.format(ticket.getDate_in()));
        String dr;
        if (ticket.getDuration() < 60) dr = (int) ticket.getDuration() + " m";
        else
            dr = (int) (ticket.getDuration() / 60.0) + "h " + (int) (ticket.getDuration() % 60.0) + " m";
        date_check_out.setText(dateFormat.format(ticket.getDate_out()));
        duration.setText(dr);
        String hours;

        hours = ticket.getDuration_paying_for() == 1 ? " hour" : " hours";
        duration_paying_for.setText((ticket.getDuration_paying_for()) + hours);
        BigDecimal discountedPrice;
        BigDecimal originakPrice = ticket.getPrice();
        discount.setText("Discount (" + (ticket.getDiscount() > 0 ? ticket.getDiscount() + "%" : "unavailable") + ")");

        discountedPrice = originakPrice.subtract(originakPrice.multiply(new BigDecimal(ticket.getDiscount())).divide(new BigDecimal("100"), RoundingMode.HALF_UP));
        price.setText("£" + discountedPrice);
        discountFrom.setText("£" + originakPrice);
        discountSave.setText("£" + (originakPrice.subtract(discountedPrice)));
        ticket.setPrice(discountedPrice);
        if (ticket.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            payButton.setText("VALIDATE");
            location = Scenes.FINISH;
            ticket.setPaid(true);
        } else {
            payButton.setText("PAY");
            location = Scenes.PAYMENT_METHODS;
        }
    }

    /**
     * Go to payment or if the ticket has the price 0, allow to leave
     */
    public void goToPayment() {
        mc.sceneManager.changeTo(location);
    }

    /**
     * Go back to the previous scene
     *
     * @since 1.0.0
     */
    public void back() {
        mc.sceneManager.changeTo(Scenes.FINISH);
    }

}
