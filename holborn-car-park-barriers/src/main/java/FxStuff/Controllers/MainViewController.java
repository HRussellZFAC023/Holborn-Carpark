package FxStuff.Controllers;


import FxStuff.GlobalVariables;
import FxStuff.InfoPopUp;
import FxStuff.SceneManager;
import FxStuff.Scenes;
import Networking.Client;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    public SceneManager sceneManager;
    Client client;
    Socket socket;
    GlobalVariables globalVariables;
    InfoPopUp popup;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour_time;
    Logger logger;
    long time_connection_starting, time_connected;


    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour_time = "";
        globalVariables = new GlobalVariables();
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
        sceneManager.changeTo(Scenes.LANDING);
    }

    public static MainViewController getInstance() {
        return instance;
    }

    @FXML
    public void switchStart(ActionEvent event) {
//        sceneManager.switchToScene("Start");
    }

    @FXML
    public void switchTicketCheck(ActionEvent event) {
        /*sceneManager.changeTo(Scenes.TICKET_CHECK);*/
    }

    @FXML
    public void goBack(ActionEvent event) {
        sceneManager.goBack();
    }

    /*public Socket getSocket() {
        return socket;
    }*/

    public void disconnectedUI(boolean enabled){
        sceneAnchor.setDisable(enabled);
    }


    /**
     * Connection to the server
     */
    private void serverConnection(){
        Client clientInterface = new Client(this);
        clientInterface.setName("ServerConnection");
        clientInterface.setDaemon(true);
        clientInterface.start();
        time_connection_starting = System.currentTimeMillis();
        client = clientInterface;
    }

    public String[] getSocket(){
        return GlobalVariables.webservice_socket.split(":");
    }

    public String getBarrier_type(){
        return GlobalVariables.barrier_type;
    }

    public SceneManager getSceneManager(){
        return sceneManager;
    }

    public InfoPopUp getPopup(){
        return popup;
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
