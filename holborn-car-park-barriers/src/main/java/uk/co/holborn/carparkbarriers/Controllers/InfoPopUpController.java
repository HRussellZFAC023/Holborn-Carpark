/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkbarriers.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;

/**
 * The info pop up controller exposes methods to update the
 * pop um message and set the indicator
 *
 * @author Vlad Alboiu
 * @version 1.0.1
 */
public class InfoPopUpController {

    @FXML
    private
    Label info;
    @FXML
    private
    ProgressIndicator indicator;

    /**
     * Set the onn screen text
     * @param message the message to be shown
     * @since 1.0.0
     */
    public void setText(String message) {
        info.setText(message);
    }

    /**
     * Set the indicator visibility
     * @param visible whether or not to display the indicator or not
     * @since 1.0.1
     */
    public void setIndicatorVisible(boolean visible) {
        if (visible) {
            info.setMinWidth(Region.USE_COMPUTED_SIZE);
        } else {
            info.setMinWidth(500);
        }
        indicator.setVisible(visible);
    }


}
