import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
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
        scenes.put("Start","/fxml/start.fxml");
        scenes.put("TicketCheck","/fxml/check_ticket.fxml");
        scenes.put("Payment","/fxml/payment.fxml");
        sceneManager = new SceneManager(sceneAnchor,scenes);

        if(happyHour){
           sceneManager.switchTo("HappyHour");
        }else{
            sceneManager.switchTo("Start");
        }
    }

    static public MainViewController getInstance(){
        return instance;
    }
    @FXML
    public void switchHappyHour(ActionEvent event){
        sceneManager.switchTo("HappyHour");
    }
    @FXML
    public void switchStart(ActionEvent event){
        sceneManager.switchTo("Start");
    }
    @FXML
    public void switchTicketCheck(ActionEvent event){
        sceneManager.switchTo("TicketCheck");
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

                String strDateFormat = "EEEE, d MMMM Y";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

                Platform.runLater(()->{
                    dateLabel.setText(dateFormat.format(dateNow));
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
