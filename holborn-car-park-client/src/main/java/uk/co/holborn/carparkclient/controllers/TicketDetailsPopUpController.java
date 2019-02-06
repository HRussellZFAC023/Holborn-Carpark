package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import uk.co.holborn.carparkclient.Scenes;
import uk.co.holborn.carparkclient.Ticket;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    ;
    MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
    }

    public void setTicket(Ticket ticket) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("EEEEEE d, MMMMM yyyy\nHH:mm");
        date_in.setText(dateFormat.format(ticket.getDate_in()));
        String dr;
        if (ticket.getDuration() < 60) dr = ticket.getDuration() + " minutes";
        else
            dr = (int) (ticket.getDuration() / 60.0) + " hours and " + (int) (ticket.getDuration() % 60.0) + " minutes";
        date_check_out.setText(dateFormat.format(ticket.getDate_out()));
        duration.setText(dr);
        price.setText("£"+ ticket.getPrice());
    }

    public void goToPayment() {
        mc.sceneManager.switchToScene(Scenes.PAYMENT_METHODS);
    }

    public void back() {
        mc.sceneManager.goBack();
    }

}
