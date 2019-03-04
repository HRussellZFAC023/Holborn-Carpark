package FxStuff.Controllers;

import FxStuff.GlobalVariables;
import FxStuff.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;



/**
 * Landing_out page provides the default "home screen" for the in barrier app. Providing paths
 * to all the other relevant scenes
 *
 * @author Cameron
 * @version 1.0.3
 */
public class LandingOutPageController implements Initializable {

    @FXML
    private
    Label welcome;

    private MainViewController mainCont;

    /**
     * Constructor that passes in teh mainViewController for referencing
     *
     * @param mainCont The mainViewController
     * @since 1.0.0
     */
    public LandingOutPageController(MainViewController mainCont){
        Logger logger = LogManager.getLogger(getClass().getName());
        this.mainCont = mainCont;
    }

    /**
     * Method that prepares the ui
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Logger logger = LogManager.getLogger(getClass().getName());
        welcome.setText(GlobalVariables.LANDING_PAGE_WELCOME);
    }

    /**
     * Method that changes the scene to the ticket check.
     * It also starts the session countdown
     *
     * @since 1.0.3
     */
    @FXML
    public void checkTicket() {
        mainCont.getSceneManager().changeTo(Scenes.TICKET_CHECK);
        mainCont.startSession();
    }

    /**
     * Method that changes the scene to the smartcard check.
     * It also starts the session countdown
     *
     * @since 1.0.0
     */
    @FXML
    public void checkSmartcard() {
        mainCont.getSceneManager().changeTo(Scenes.SMARTCARD_CHECK);
        mainCont.startSession();
    }
}
