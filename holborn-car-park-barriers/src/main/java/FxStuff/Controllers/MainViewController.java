package FxStuff.Controllers;


import FxStuff.*;
import Networking.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    private Client client;
    private InfoPopUp popup;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour_time;
    private Logger logger;


    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour_time = "";
        new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        instance = this;
    }

    public void sendAlert(String title, String header, String content, AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
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
        Client clientInterface = new Client(this);
        clientInterface.setName("ServerConnection");
        clientInterface.setDaemon(true);
        clientInterface.start();
        client = clientInterface;
    }

    public String[] getSocket() {
        return GlobalVariables.webservice_socket.split(":");
    }

    public String getBarrier_type() {
        return GlobalVariables.barrier_type;
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

    public Object[] getCarparkDetails() {
        return client.getCarparkDetails();
    }

    public Ticket getNewTicket() {
        return client.getTicket();
    }

    public void disconnect() {
        if (client.isConnected()) {
            client.endConnection();
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

}
