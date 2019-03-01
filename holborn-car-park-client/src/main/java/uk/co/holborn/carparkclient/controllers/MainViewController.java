package uk.co.holborn.carparkclient.controllers;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    AnchorPane sceneContainer;
    @FXML
    Button themeModeButton;
    public SceneManager sceneManager;
    private Socket socket;
    private GlobalVariables globalVariables;
    private InfoPopUp popup;
    public Ticket ticket;
    String hourly_price;
    String parking_spaces;
    String happy_hour_time;
    private Logger logger;
    public boolean happyHour = false;
    private Long sessionStartTime;
    public static SpriteSheets spriteSheets;
    int pastHour = 0;


    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour_time = "";
        globalVariables = new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        try {
            socket = IO.socket(GlobalVariables.WEBSERVICE_SOCKET);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        instance = this;
        spriteSheets = new SpriteSheets();
        spriteSheets.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updater();
        themeModeButton.setText("NIGHTTIME");
        popup = new InfoPopUp(sceneContainer);
        sceneManager = new SceneManager(sceneAnchor);
        sceneManager.changeTo(Scenes.LANDING);
        sceneAnchor.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> sessionStartTime = System.currentTimeMillis());
        socketPreparation();
    }

    private void socketPreparation() {
        socket.on(Socket.EVENT_CONNECT, args_cn -> {
            logger.info("Connected to the web server. Authorising...");
            popup.show("Connected! Authorising...");
            disconnectedUI(true);
            socket.emit("authorisation", GlobalVariables.CAR_PARK_ID,GlobalVariables.CAR_PARK_NAME, (Ack) objects -> {
                if (objects[0].equals(200)) {
                    popup.show("Authorised", false);
                    popup.removePopUp();
                    logger.info("Authorised!");
                    LandingPageController lc = (LandingPageController) Scenes.LANDING.getController();
                    Platform.runLater(lc::enableFetching);
                    disconnectedUI(false);
                } else {
                    logger.error("Unauthorised access! Please check that the information from the config file are correct or check the database connection.");
                    popup.removePopUp();
                    Platform.runLater((() -> {
                        Alerter.showUnableToStartAlert(
                                "Unauthorised access!",
                                "You are not authorised to access the web service. " +
                                        "Please make sure the configuration details given by your administrator are correct and try again."
                        );
                    }));

                }
            });
        });
        socket.on(Socket.EVENT_CONNECTING, args_cni -> {
            popup.show("Connecting...");
            disconnectedUI(true);
        });
        socket.on(Socket.EVENT_RECONNECTING, args_cni -> {
            popup.show("Reconnecting...");
            disconnectedUI(true);
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args_cni -> System.out.println("Err" + args_cni[0]));
        socket.on(Socket.EVENT_DISCONNECT, args_dc -> {
            logger.warn("Disconnected");
            disconnectedUI(true);
            sceneManager.changeTo(Scenes.LANDING);
            popup.show("Disconnected");
        });
        socket.connect();
    }

    public static MainViewController getInstance() {
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public SpriteSheets getSpriteSheets() {
        return spriteSheets;
    }

    private void disconnectedUI(boolean enabled) {
        sceneAnchor.setDisable(enabled);
    }

    /**
     * Thread that changes the scene to the landing page when no interaction with the ui happened for a defined time
     */
    void startSession() {
        sessionStartTime = System.currentTimeMillis();
        int session_timeout_ms = GlobalVariables.SESSION_TIMEOUT_S * 1000;
        int session_timeout_popup_ms = GlobalVariables.SESSION_TIMEOUT_POPUP_DURATION_S * 1000;
        Thread session = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (sceneManager.getCurrentScene() == Scenes.LANDING || sceneManager.getCurrentScene() == Scenes.FINISH) {
                    Thread.currentThread().interrupt();
                }
                if ((System.currentTimeMillis()) - sessionStartTime >= session_timeout_ms) {
                    popup.show("Session timed out", false);
                    sceneAnchor.setDisable(true);
                    sceneManager.changeTo(Scenes.FINISH);
                    try {
                        Thread.sleep(session_timeout_popup_ms);
                    } catch (InterruptedException ignored) {
                    }
                    popup.removePopUp();
                    sceneAnchor.setDisable(false);
                    Thread.currentThread().interrupt();
                } else {
                    try {
                        Thread.sleep(1000);
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
     * Thread that updates the date and time
     */
    private void updater() {
//        activate to see fps
//         final long[] frameTimes = new long[100];
//        final int[] frameTimeIndex = {0};
//        final boolean[] arrayFilled = {false};
        pastHour = -1;

        AnimationTimer at = new AnimationTimer() {
            @Override
            public void handle(long now) {
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String date = new SimpleDateFormat("MMM d Y").format(new Date()).toUpperCase();
                dateLabel.setText(date);
                timeLabel.setText(time);
                Calendar currTime = Calendar.getInstance();
                int hour = currTime.get(Calendar.HOUR_OF_DAY);
                int minute = currTime.get(Calendar.MINUTE);

                if (GlobalVariables.AUTO_NIGHT_TIME)
                    if (pastHour != hour) {
                        pastHour = hour;
                        boolean sameDay = GlobalVariables.NIGHT_TIME_START < GlobalVariables.NIGHT_TIME_END;
                        boolean hourBetweenSameDay = hour >= GlobalVariables.NIGHT_TIME_START && hour <= GlobalVariables.NIGHT_TIME_END;
                        boolean hourBetweenSameAndNextDay = hour >= GlobalVariables.NIGHT_TIME_START || hour < GlobalVariables.NIGHT_TIME_END;
                        boolean expr = sameDay ? hourBetweenSameDay : hourBetweenSameAndNextDay;
                        if (expr) {
                            ThemeProvider.getInstance().switchTheme(Themes.DARK);
                        } else {
                            ThemeProvider.getInstance().switchTheme(Themes.LIGHT);
                        }
                        updateThemeButton();
                    }

                //activate to see FPS
//                long oldFrameTime = frameTimes[frameTimeIndex[0]] ;
//                frameTimes[frameTimeIndex[0]] = now ;
//                frameTimeIndex[0] = (frameTimeIndex[0] + 1) % frameTimes.length ;
//                if (frameTimeIndex[0] == 0) {
//                    arrayFilled[0] = true ;
//                }
//                if (arrayFilled[0]) {
//                    long elapsedNanos = now - oldFrameTime ;
//                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
//                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
//                    dateLabel.setText(String.format("FPS: %.3f", frameRate) + " " + date);
//                }
            }
        };
        at.start();
    }

    @FXML
    void switchTheme() {
        if (ThemeProvider.getInstance().getCurrentTheme() == Themes.LIGHT) {
            ThemeProvider.getInstance().switchTheme(Themes.DARK);
        } else {
            ThemeProvider.getInstance().switchTheme(Themes.LIGHT);
        }
        updateThemeButton();
    }

    private void updateThemeButton() {
        if (ThemeProvider.getInstance().getCurrentTheme() == Themes.DARK) {
            themeModeButton.setText("DAYTIME");
        } else {
            themeModeButton.setText("NIGHTTIME");
        }
    }

    void emitTicketPaid() {
        Object[] params = new Object[]{true, "" + ticket.getDuration(), "" + ticket.getDate_out(), "" + ticket.get_id(), "" + ticket.getPrice()};
        socket.emit("ticket-paid", params);
    }

}
