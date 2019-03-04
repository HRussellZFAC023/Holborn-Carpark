package FxStuff.Controllers;


import FxStuff.*;
import FxStuff.Sprites.SpriteSheets;
import Networking.Barrier;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private long CONNECTION_TIME_MS = 1000;
    private static MainViewController instance = null;

    @FXML
    Label companyName;
    @FXML
    Label dateLabel;
    @FXML
    Label timeLabel;
    @FXML
    AnchorPane sceneAnchor;
    @FXML
    AnchorPane mainAnchor;
    @FXML
    AnchorPane blurrAnchor;
    private SceneManager sceneManager;
    private Barrier barrier;
    private InfoPopUp popup;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour_time;
    private SpriteSheets spriteSheets;
    private Logger logger;


    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour_time = "";
        new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        spriteSheets = new SpriteSheets();
        spriteSheets.load();
        instance = this;
    }

    /**
     * Method that returns the sprite sheets
     *
     * @return sprite sheets
     * @since 1.0.5
     */
    public SpriteSheets getSpriteSheets() {
        return spriteSheets;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updater();
        Platform.runLater(this::serverConnection);
        popup = new InfoPopUp(mainAnchor);
        sceneManager = new SceneManager(sceneAnchor);
        if (GlobalVariables.getBarrierType()) sceneManager.changeTo(Scenes.LANDING_IN);
        else sceneManager.changeTo(Scenes.LANDING_OUT);
    }

    public static MainViewController getInstance() {
        return instance;
    }

    public void disconnectedUI(boolean enabled) {
        sceneAnchor.setDisable(enabled);
    }

    /**
     * Connection to the server
     */
    private void serverConnection() {
        Barrier barrierInterface = new Barrier(this);
        barrierInterface.setName("ServerConnection");
        barrierInterface.setDaemon(true);
        barrierInterface.start();
        barrier = barrierInterface;
    }

    public String[] getSocket() {
        return GlobalVariables.SOCKET_ADDRESS.split(":");
    }

    public String getBarrier_type() {
        return GlobalVariables.BARRIER_TYPE;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public InfoPopUp getPopup() {
        return popup;
    }

    public Logger getLogger() {
        return logger;
    }

    public String[] getCarparkDetails() {
        return barrier.getCarparkDetails();
    }

    public Ticket getNewTicket() {
        return barrier.getTicket();
    }

    public void disconnect() {
        if (barrier.isConnected()) {
            barrier.endConnection();
        }
    }

    /**
     * Update the date and time on screen
     */
    private void updater() {
        Thread updater = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Date dateNow = new Date();
                String strTimeFormat = "HH:mm:ss";
                DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);

                String strDateFormat = "MMM d Y";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

                Platform.runLater(() -> {
                    dateLabel.setText(dateFormat.format(dateNow).toUpperCase());
                    timeLabel.setText(timeFormat.format(dateNow));
                });

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        updater.setName("Thread-Date&Time Updater");
        updater.setDaemon(true);
        updater.start();
    }

    public boolean checkTicket(String ID, boolean smartcard){
        return (smartcard? barrier.validateSmartcard(ID) : barrier.checkTicket(ID));
    }

}
