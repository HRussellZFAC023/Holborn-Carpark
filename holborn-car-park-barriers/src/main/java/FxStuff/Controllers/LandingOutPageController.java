package FxStuff.Controllers;

import FxStuff.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class LandingOutPageController implements Initializable {

    private MainViewController mainCont;
    Logger logger;


    public LandingOutPageController(MainViewController mainCont){
        this.mainCont = mainCont;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass().getName());
    }


    @FXML
    public void checkTicket() {
        mainCont.getSceneManager().changeTo(Scenes.TICKET_CHECK);
        System.out.println("Scan ticket");
    }

    @FXML
    public void checkSmartcard() {
        mainCont.getSceneManager().changeTo(Scenes.SMARTCARD_CHECK);
        System.out.println("Scan smartcard");
    }
}
