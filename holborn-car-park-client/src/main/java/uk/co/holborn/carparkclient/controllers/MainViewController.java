package uk.co.holborn.carparkclient.controllers;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
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
    Ticket ticket;


    public MainViewController() {
        globalVariables = new GlobalVariables();
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
        scenes.put("TicketCheck", "/fxml/check_ticket.fxml");
        scenes.put("PaymentMethod", "/fxml/payment_methods.fxml");
        scenes.put("Payment", "/fxml/payment_method_cash.fxml");
        sceneManager = new SceneManager(sceneAnchor, scenes);

        sceneManager.switchToScene("Start");
        try {
            socket = IO.socket(GlobalVariables.webservice_socket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, args_cn -> {
            System.out.println("Connected to the web service");
            popup.show("Connected");
            socket.emit("authorise", GlobalVariables.car_park_id);
            popup.removePopUp();
        });
        socket.on(Socket.EVENT_CONNECTING, args_cni -> {
            popup.show("Connecting...");
            System.out.println("Connecting...");
        });
        socket.on(Socket.EVENT_RECONNECTING, args_cni -> {
            popup.show("Reconnecting...");
            System.out.println("Reconnecting...");
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args_cni -> {
            System.out.println("Err" + args_cni[0]);
        });
        socket.on(Socket.EVENT_DISCONNECT, args_dc -> {
            System.out.println("Disconnected from the web service");
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
        sceneManager.switchToScene("TicketCheck");
    }

    @FXML
    public void goBack(ActionEvent event) {
        sceneManager.goBack();
    }

    Socket getSocket() {
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
