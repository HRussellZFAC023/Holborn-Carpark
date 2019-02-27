package FxStuff.Controllers;

import FxStuff.Animator;
import FxStuff.GlobalVariables;
import FxStuff.Scenes;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class LandingInPageController implements Initializable {

    private MainViewController mainCont;
    private Logger logger;

    @FXML
    Label welcome;
    @FXML
    Label parking_spaces;
    @FXML
    Label price;
    @FXML
    Label happy_hour;


    public LandingInPageController(MainViewController mainCont){
        this.mainCont = mainCont;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass().getName());
        welcome.setText(GlobalVariables.landing_page_welcome);
        Animator.nodeFade(welcome, true);
    }

    public void enableFetching(){
        String fetching = "Fetching...";
        if (mainCont.parking_spaces.isEmpty()) updateTextParkingSpaces(fetching);
        else parking_spaces.setText(mainCont.parking_spaces);
        if (mainCont.hourly_price.isEmpty()) updateTextPrice(fetching);
        else price.setText(mainCont.hourly_price);
        if (mainCont.happy_hour_time.isEmpty()) updateHappyHour(fetching);
        else happy_hour.setText(mainCont.happy_hour_time);
        update(mainCont.getCarparkDetails());
    }

    private void update(Object[] objects) {
        Platform.runLater(() -> {
            System.out.println("Updating");
            updateTextParkingSpaces(objects[0] + "");
            updateTextPrice("£" + objects[1]);
            updateHappyHour((objects[2] + "").subSequence(0, 5) + " - " + (objects[3] + "").subSequence(0, 5));
        });
    }

    private void updateTextParkingSpaces(String message) {
        if (!mainCont.parking_spaces.equals(message)) {
            mainCont.parking_spaces = message;
            Animator.nodeFade(parking_spaces, false);
            parking_spaces.setText(message);
            Animator.nodeFade(parking_spaces, true);
        }
    }

    private void updateTextPrice(String message) {
        if (!mainCont.hourly_price.equals(message)) {
            mainCont.hourly_price = message;
            Animator.nodeFade(price, false);
            price.setText(message);
            Animator.nodeFade(price, true);
        }
    }

    private void updateHappyHour(String message) {
        if (!mainCont.happy_hour_time.equals(message)) {
            mainCont.happy_hour_time = message;
            Animator.nodeFade(happy_hour, false);
            happy_hour.setText(message);
            Animator.nodeFade(happy_hour, true);
        }
    }

    @FXML
    public void begin() {
        mainCont.getSceneManager().changeTo(Scenes.PRINT_TICKET);
    }
}
