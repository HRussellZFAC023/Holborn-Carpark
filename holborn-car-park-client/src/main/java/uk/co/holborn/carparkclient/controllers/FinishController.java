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

/**
 * The finish controller handles the interactions of the finish UI.
 * The controller will inform the user of any amount added by them without
 * finishing the payment (and giving them back), inform the user of the change and if the ticket or
 * smart card parking session has been paid.
 * This controller has a countdown of a specified amount in {@link GlobalVariables}. After that countdown,
 * the scene will automatically switch back to the landing page.
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class FinishController implements Initializable {
    @FXML
    private
    Label infoText;
    @FXML
    private
    Label aditionalInfo;
    @FXML
    private
    ImageView ticket_image;
    private final MainViewController mc;
    private Sprite sprite;
    private Ticket ticket;
    private Thread t;

    /**
     * Constructor
     */
    public FinishController() {
        Logger logger = LogManager.getLogger(getClass().getName());
        mc = MainViewController.getInstance();
    }

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sprite = new Sprite(ticket_image, mc.getSpriteSheets().getSpriteSettings(Sprites.THANK_YOU));
        ticket_image.setCache(true);
        ticket_image.setCacheHint(CacheHint.SPEED);

    }

    /**
     * This method prepares the ui before showing, resenting the sprite,
     * clearing the textfield, etc.
     * This is called every time the scene manager switches to this scene.
     */
    public void setup() {
        sprite.resetView();
        startFinishedChangeDelay();
        setMessage();
        animateImageShow();
        ticket = mc.ticket;
        setAditionalMessage("");
        if (ticket != null) {
            if (ticket.isPaidOnReceived()) {
                setAditionalMessage("Your " + (ticket.isReceivedFromSmartcard() ? "smart card parking session" : "ticket") + " has been already paid!");
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

    /**
     * This method called by the next customer button will change to the landing page
     */
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
                    Platform.runLater(() -> mc.sceneManager.reverseTo(Scenes.LANDING));
                    Thread.currentThread().interrupt();
                }
            }
        });
        t.setName("Thread - Finished Screen Change Delay");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Animate image showing in
     */
    private void animateImageShow() {
        Animator.nodePopIn(ticket_image, 0.6, e -> sprite.replay(1));
    }

    /**
     * Inform the user with a message
     */
    private void setMessage() {
        infoText.setText("Thank you for visiting us! Have a safe journey!");
        Animator.nodeFade(infoText, true);
    }

    /**
     * Set the additional message to inform the user with
     * @param message he message to inform the user with
     */
    private void setAditionalMessage(String message) {
        aditionalInfo.setText(message);
        Animator.nodeFade(infoText, true);
    }

    /**
     * Emit a paid event to the socket that
     * updates the values in the database  by the webservice
     */
    private void emitTicketPaid() {
        Object[] params = new Object[]{true, "" + ticket.getDuration(), "" + ticket.getDate_out(), "" + ticket.get_id(), "" + ticket.getPrice()};
        mc.getSocket().emit("ticket-paid", params);
    }
}
