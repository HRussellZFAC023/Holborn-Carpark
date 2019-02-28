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

public class TicketDetailsPopUpController implements Initializable {

    @FXML
    Label date_in;
    @FXML
    Label date_check_out;
    @FXML
    Label price;
    @FXML
    Label duration;
    @FXML
    Label duration_paying_for;
    @FXML
    Label discount;
    @FXML
    Label discountFrom;
    @FXML
    Label discountSave;
    @FXML
    Button payButton;
    MainViewController mc;
    Scenes location;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
    }

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
        BigDecimal discountedPrice = BigDecimal.ZERO;
        BigDecimal originakPrice = ticket.getPrice();
        discount.setText("Discount (" + (ticket.getDiscount() > 0 ? ticket.getDiscount() + "%)" : "unavailable") + ")");

        discountedPrice = originakPrice.subtract(originakPrice.multiply(new BigDecimal(ticket.getDiscount())).divide(new BigDecimal("100"), RoundingMode.HALF_UP));
        price.setText("£" + discountedPrice);
        discountFrom.setText("£" + originakPrice);
        discountSave.setText("£" + (originakPrice.subtract(discountedPrice)));
        ticket.setPrice(discountedPrice);
        if(ticket.getPrice()== BigDecimal.ZERO){
            payButton.setText("VALIDATE");
            location = Scenes.FINISH;
            ticket.setPaid(true);
        }else{
            payButton.setText("PAY");
            location = Scenes.PAYMENT_METHODS;
        }
    }

    public void goToPayment() {
        mc.sceneManager.changeTo(location);
    }

    public void back() {
        mc.sceneManager.goBack();
    }

}
