package uk.co.holborn.carparkclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LandingPageController implements Initializable {

   private MainViewController mc;
    @FXML
    Label welcome;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        welcome.setText(GlobalVariables.landing_page_welcome);

    }

    @FXML
    public void begin(){
        mc.sceneManager.switchToScene("TicketCheck");
    }

}
