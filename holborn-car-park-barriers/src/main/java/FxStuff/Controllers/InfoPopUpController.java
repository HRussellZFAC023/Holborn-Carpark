package FxStuff.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

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
        if(visible) {
            indicator.setPrefHeight(200);
            indicator.setPrefWidth(120);
        }else{
            indicator.setPrefHeight(0);
            indicator.setPrefWidth(0);
        }
    }


}
