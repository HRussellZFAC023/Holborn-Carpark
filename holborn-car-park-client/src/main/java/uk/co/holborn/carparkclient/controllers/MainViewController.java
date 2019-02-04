package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.ConnectionPopUp;
import uk.co.holborn.carparkclient.GlobalVariables;
import uk.co.holborn.carparkclient.SceneManager;
import uk.co.holborn.carparkclient.Ticket;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    ConnectionPopUp popup;
    public Ticket ticket;
    public String hourly_price;
    public String parking_spaces;
    Logger logger;
    public boolean happyHour = false;


    public MainViewController() {
        globalVariables = new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        try {
            socket = IO.socket(GlobalVariables.webservice_socket);
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
        popup = new ConnectionPopUp(mainAnchor, blurrAnchor);
        HashMap<String, String> scenes = new HashMap<>();
        scenes.put("HappyHour", "/fxml/happy_hour_view.fxml");
        scenes.put("Start", "/fxml/landing_page.fxml");
        scenes.put("TicketCheck", "/fxml/ticket_checkl.fxml");
        scenes.put("PaymentMethod", "/fxml/payment_methods.fxml");
        scenes.put("Payment", "/fxml/payment_method_cash.fxml");
        sceneManager = new SceneManager(sceneAnchor, scenes);

        socket.on(Socket.EVENT_CONNECT, args_cn -> {
            logger.info("Connected to the web server. Authorising...");
            socket.emit("authorisation", GlobalVariables.car_park_id, (Ack) objects -> {
                if (objects[0].equals(200)) {
                    popup.show("Connected");
                    popup.removePopUp();
                    logger.info("Authorised!");
                    sceneManager.switchToScene("Start");
                } else {
                    logger.error("Unauthorised access! Please check that the information from the config file are correct or check the database connection.");
                    System.exit(0);
                }
            });
        });
        socket.on(Socket.EVENT_CONNECTING, args_cni -> {
            popup.show("Connecting...");
            logger.info("Connecting...");
        });
        socket.on(Socket.EVENT_RECONNECTING, args_cni -> {
            popup.show("Reconnecting...");
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args_cni -> {
            System.out.println("Err" + args_cni[0]);
        });
        socket.on(Socket.EVENT_DISCONNECT, args_dc -> {
            logger.warn("Disconnected");
            popup.show("Disconnected");
        });
        socket.connect();

    }

    public static MainViewController getInstance() {
        return instance;
    }

    @FXML
    public void switchHappyHour(ActionEvent event) {
        sceneManager.switchToScene("HappyHour");
    }

    @FXML
    public void switchStart(ActionEvent event) {
        sceneManager.switchToScene("Start");
    }

    @FXML
    public void switchTicketCheck(ActionEvent event) {
        socket.emit("hi");
        sceneManager.switchToScene("TicketCheck");
    }

    @FXML
    public void goBack(ActionEvent event) {
        sceneManager.goBack();
    }

    public Socket getSocket() {
        return socket;
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
        updater.setDaemon(true);
        updater.start();
    }


}
