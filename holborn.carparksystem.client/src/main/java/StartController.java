import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();

    }

    @FXML
    public void begin(){
        mc.sceneManager.switchTo("TicketCheck");
    }




}
