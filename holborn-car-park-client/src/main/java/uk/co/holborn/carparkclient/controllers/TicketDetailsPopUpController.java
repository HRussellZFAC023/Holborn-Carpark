package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;

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
    MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
    }

    public void setTicket(Ticket ticket) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEEE d, MMMMM yyyy\nHH:mm");
        date_in.setText(dateFormat.format(ticket.getDate_in()));
        String dr;
        if (ticket.getDuration() < 60) dr = ticket.getDuration() + " minutes";
        else
            dr = (int) (ticket.getDuration() / 60.0) + " hours and " + (int) (ticket.getDuration() % 60.0) + " minutes";
        date_check_out.setText(dateFormat.format(ticket.getDate_out()));
        duration.setText(dr);
        String hours;

            hours = ticket.getDuration_paying_for()==1 ? " hour": " hours";
        duration_paying_for.setText((ticket.getDuration_paying_for())+ hours);
        price.setText("£" + ticket.getPrice());
    }

    public void goToPayment() {
        mc.sceneManager.changeTo(Scenes.PAYMENT_METHODS);
    }

    public void back() {
        mc.sceneManager.goBack();
    }

}
