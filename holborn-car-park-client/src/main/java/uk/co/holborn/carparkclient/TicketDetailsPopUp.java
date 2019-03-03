/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.TicketDetailsPopUpController;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;

/**
 * Class that implements a easy way to display ticket informations on screen
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class TicketDetailsPopUp {
    private final AnchorPane blurrAnchor;
    private final AnchorPane mainAnchor;
    private AnchorPane root;
    private TicketDetailsPopUpController tc;
    private boolean alreadyOn;

    /**
     * The constructor takes two parameters
     *
     * @param mainAnchor  the anchor for the ticket detail pane to be a child of
     * @param blurrAnchor the anchor that will get blurred on show
     */
    public TicketDetailsPopUp(AnchorPane mainAnchor, AnchorPane blurrAnchor) {
        this.mainAnchor = mainAnchor;
        this.blurrAnchor = blurrAnchor;
        alreadyOn = false;
        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ticket_details_popup.fxml"));
            try {
                tc = new TicketDetailsPopUpController();
                loader.setController(tc);
                root = loader.load();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        root.setCache(true);
        root.setCacheHint(CacheHint.SPEED);
        blurrAnchor.setCache(true);
        blurrAnchor.setCacheHint(CacheHint.SPEED);
    }

    /**
     * Removes the pane from the screen
     */
    public void remove() {
        blurrAnchor.setEffect(null);
        alreadyOn = false;
        if (mainAnchor.getChildren().size() > 1) mainAnchor.getChildren().remove(1);
    }

    /**
     * Displays the ticket details popup for a specific ticket
     *
     * @param ticket the ticket for which the information will be displayed
     */
    public void show(Ticket ticket) {
        boolean debug_mode = false;
        if (!debug_mode)
            if (!alreadyOn) {
                setBottomAnchor(root, 0.0);
                setRightAnchor(root, 0.0);
                setLeftAnchor(root, 0.0);
                setTopAnchor(root, 0.0);
                tc.setTicket(ticket);
                Platform.runLater(() -> mainAnchor.getChildren().add(root));
                Animator.nodeBlurrBackgroundAndShowPopUp(blurrAnchor, root, null);
                alreadyOn = true;
            }


    }
}
