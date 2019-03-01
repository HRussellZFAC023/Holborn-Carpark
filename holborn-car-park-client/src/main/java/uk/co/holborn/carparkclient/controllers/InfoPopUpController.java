package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoPopUpController implements Initializable {

    @FXML
    Label info;
    @FXML
    ProgressIndicator indicator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setText(String message) {
        info.setText(message);
    }

    public void setIndicatorVisible(boolean visible) {
        if (visible) {
            info.setMinWidth(Region.USE_COMPUTED_SIZE);
        } else {
            info.setMinWidth(500);
        }
        indicator.setVisible(visible);
    }


}
