package uk.co.holborn.carparkclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class FinishController implements Initializable {
    @FXML
    Label infoText;
    @FXML
    Label aditionalInfo;
    @FXML
    ImageView ticket_image;
    private Logger logger;
    private MainViewController mc;
    Sprite sprite;
    Ticket ticket;
    Thread t;

    public FinishController() {
        logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sprite = new Sprite(ticket_image, mc.getSpriteSheets().getSpriteSettings(Sprites.THANK_YOU));
        ticket_image.setCache(true);
        ticket_image.setCacheHint(CacheHint.SPEED);

    }

    public void setup() {
        sprite.resetView();
        startFinishedChangeDelay();
        setMessage("Thank you for visiting us! Have a safe journey!");
        animateImageShow();
         ticket= mc.ticket;
        setAditionalMessage("");
        if (ticket != null) {
            if (ticket.isPaidOnReceived()) {
                setAditionalMessage("Your " + (ticket.isReceivedFromSmartcard() ? "smart card session" : "ticket") + " is already paid!");
            } else if (ticket.isPaid()) {
                if (ticket.getChange().compareTo(BigDecimal.ZERO) > 0) {
                    setAditionalMessage("Make sure to take your change of £" + ticket.getChange() + ".");
                }
                emitTicketPaid();
            } else if (ticket.getAmountInTicketMachine().compareTo(BigDecimal.ZERO) > 0) {
                setAditionalMessage("Don't forget to take the amount you inserted of £" + ticket.getAmountInTicketMachine() + ".");
            }

        }
    }

    @FXML
    private void nextCustomer() {
        t.interrupt();
        mc.sceneManager.reverseTo(Scenes.LANDING);
    }

    private void startFinishedChangeDelay() {
        long startTime = System.currentTimeMillis();
        int finishTime = GlobalVariables.TRANSACTION_FINISHED_DELAY_S * 1000;
        t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if ((System.currentTimeMillis()) - startTime >= finishTime) {
                    Platform.runLater(() -> {
                        mc.sceneManager.reverseTo(Scenes.LANDING);
                    });
                    Thread.currentThread().interrupt();
                }
            }
        });
        t.setName("Thread - Finished Screen Change Delay");
        t.setDaemon(true);
        t.start();
    }

    private void animateImageShow() {
        Animator.nodePopIn(ticket_image, 0.6, e -> {
            sprite.replay(1);
        });
    }

    private void setMessage(String message) {
        infoText.setText(message);
        Animator.nodeFade(infoText, true);
    }

    private void setAditionalMessage(String message) {
        aditionalInfo.setText(message);
        Animator.nodeFade(infoText, true);
    }

    void emitTicketPaid() {
        Object[] params = new Object[]{true, "" + ticket.getDuration(), "" + ticket.getDate_out(), "" + ticket.get_id(), "" + ticket.getPrice()};
        mc.getSocket().emit("ticket-paid", params);
    }
}
