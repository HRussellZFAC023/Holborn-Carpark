package uk.co.holborn.carparkclient;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.layout.AnchorPane;
import uk.co.holborn.carparkclient.controllers.TicketDetailsPopUpController;

import java.io.IOException;

import static javafx.scene.layout.AnchorPane.*;

public class TicketDetailsPopUp {
    private AnchorPane blurrAnchor;
    private AnchorPane mainAnchor;
    private AnchorPane root;
    private TicketDetailsPopUpController tc;
    private boolean alreadyOn;
    private boolean debug_mode = false;

    public TicketDetailsPopUp(AnchorPane mainAnchor, AnchorPane blurrAnchor) {
        this.mainAnchor = mainAnchor;
        this.blurrAnchor = blurrAnchor;
        alreadyOn = false;
        if (root == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ticket_details_popup.fxml"));
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

    public void remove() {
        blurrAnchor.setEffect(null);
        alreadyOn = false;
        if (mainAnchor.getChildren().size() > 1) mainAnchor.getChildren().remove(1);
    }

    public void show(Ticket ticket) {
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
