package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionPopUpController implements Initializable {

    @FXML
    Label info;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setText(String message) {
        info.setText(message);
    }


}
