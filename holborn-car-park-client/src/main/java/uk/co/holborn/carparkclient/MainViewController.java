package uk.co.holborn.carparkclient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainViewController implements Initializable {

    @FXML
    Label companyName;
    @FXML
    Label dateLabel;
    @FXML
    Label timeLabel;
    @FXML
    AnchorPane sceneAnchor;
    public static SceneManager sceneManager;
    boolean happyHour = false;
   static MainViewController instance = null;
    public MainViewController() {
        instance = this;
    }

    public void sendAlert(String title, String header, String content, AlertType alertType) {
        Platform.runLater(()-> {
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
        HashMap<String, String> scenes = new HashMap<String, String>();
        scenes.put("HappyHour", "/fxml/happy_hour_view.fxml");
        scenes.put("Start","/fxml/landing_page.fxml");
        scenes.put("TicketCheck","/fxml/check_ticket.fxml");
        scenes.put("Payment","/fxml/payment.fxml");
        sceneManager = new SceneManager(sceneAnchor,scenes);

        if(happyHour){
           sceneManager.switchToScene("HappyHour");
        }else{
            sceneManager.switchToScene("Start");
        }
    }

    static public MainViewController getInstance(){
        return instance;
    }
    @FXML
    public void switchHappyHour(ActionEvent event){
        sceneManager.switchToScene("HappyHour");
    }
    @FXML
    public void switchStart(ActionEvent event){
        sceneManager.switchToScene("Start");
    }
    @FXML
    public void switchTicketCheck(ActionEvent event){
        sceneManager.switchToScene("TicketCheck");
    }
    @FXML
    public void goBack(ActionEvent event){
        sceneManager.goBack();
    }
    /**
     * Update the date and time on screen
     */
    private void updater(){
        Thread updater = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()){
                Date dateNow = new Date();
                String strTimeFormat = "HH:mm:ss";
                DateFormat timeFormat = new SimpleDateFormat(strTimeFormat);

                String strDateFormat = "MMM d Y";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

                Platform.runLater(()->{
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
