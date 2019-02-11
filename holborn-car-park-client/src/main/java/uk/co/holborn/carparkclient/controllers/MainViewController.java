package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


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
    Socket socket;
    GlobalVariables globalVariables;
    InfoPopUp popup;
    public Ticket ticket;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour_time;
    Logger logger;
    public boolean happyHour = false;
    Long sessionStartTime;


    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour_time = "";
        globalVariables = new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        try {
            socket = IO.socket(GlobalVariables.WEBSERVICE_SOCKET);
            ;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        popup = new InfoPopUp(mainAnchor);
        sceneManager = new SceneManager(sceneAnchor);
        sceneManager.changeTo(Scenes.LANDING);
        sceneAnchor.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            sessionStartTime = System.currentTimeMillis();
        });
        socketPreparation();


    }

    private void socketPreparation() {
        socket.on(Socket.EVENT_CONNECT, args_cn -> {
            logger.info("Connected to the web server. Authorising...");
            popup.show("Connected! Authorising...");
            disconnectedUI(true);
            socket.emit("authorisation", GlobalVariables.CAR_PARK_ID, (Ack) objects -> {
                if (objects[0].equals(200)) {
                    popup.show("Authorised", false);
                    popup.removePopUp();
                    logger.info("Authorised!");
                    LandingPageController lc = (LandingPageController) Scenes.LANDING.getController();
                    Platform.runLater(lc::enableFetching);
                    disconnectedUI(false);
                } else {
                    logger.error("Unauthorised access! Please check that the information from the config file are correct or check the database connection.");
                    System.exit(0);
                }
            });
        });
        socket.on(Socket.EVENT_CONNECTING, args_cni -> {
            popup.show("Connecting...");
            disconnectedUI(true);
            // logger.info("Connecting...");
        });
        socket.on(Socket.EVENT_RECONNECTING, args_cni -> {
            popup.show("Reconnecting...");
            disconnectedUI(true);
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args_cni -> {
            System.out.println("Err" + args_cni[0]);
        });
        socket.on(Socket.EVENT_DISCONNECT, args_dc -> {
            logger.warn("Disconnected");
            disconnectedUI(true);
            popup.show("Disconnected");
        });
        socket.connect();
    }

    public static MainViewController getInstance() {
        return instance;
    }

    @FXML
    public void switchHappyHour(ActionEvent event) {
//        sceneManager.switchToScene("HappyHour");
    }

    @FXML
    public void switchStart(ActionEvent event) {
//        sceneManager.switchToScene("Start");
    }

    @FXML
    public void switchTicketCheck(ActionEvent event) {
        sceneManager.changeTo(Scenes.TICKET_CHECK);
    }

    @FXML
    public void goBack(ActionEvent event) {
        sceneManager.goBack();
    }

    public Socket getSocket() {
        return socket;
    }

    public void disconnectedUI(boolean enabled) {
        sceneAnchor.setDisable(enabled);
    }

    public void sessionTimeOut() {
        sessionStartTime = System.currentTimeMillis();
        int session_timeout_ms = GlobalVariables.SESSION_TIMEOUT_S * 1000;
        int session_timeout_popup_ms = GlobalVariables.SESSION_TIMEOUT_POPUP_DURATION_S * 1000;
        Thread session = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (sceneManager.getCurrentScene() == Scenes.LANDING) {
                    Thread.currentThread().interrupt();
                }
                if ((System.currentTimeMillis()) - sessionStartTime >= session_timeout_ms) {
                    popup.show("Session timed out", false);
                    sceneAnchor.setDisable(true);
                    sceneManager.changeTo(Scenes.LANDING);
                    try {
                        Thread.sleep(session_timeout_popup_ms);
                    } catch (InterruptedException e) {
                    }
                    popup.removePopUp();
                    sceneAnchor.setDisable(false);
                    Thread.currentThread().interrupt();
                }else{
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                    }
                }
            }
        });
        session.setName("Thread-Session Timeout Checker");
        session.setDaemon(true);
        session.start();
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
                    Thread.sleep(500);
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
