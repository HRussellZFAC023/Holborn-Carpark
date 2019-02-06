package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import uk.co.holborn.carparkclient.Scenes;

import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    private MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();

    }

    @FXML
    public void begin(){
        mc.sceneManager.changeTo(Scenes.TICKET_CHECK);
    }




}
