/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

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
import uk.co.holborn.carparkclient.Networking.Server;

import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * The main view controller is the most important controller of them all.
 * It controls everything from the {@link SceneManager} to the {@link Socket} connections
 *
 * @author Vlad Alboiu
 * @version 1.0.6
 */
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
    private InfoPopUp popup;
    public Ticket ticket;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour;
    private final Logger logger;
    public boolean happyHour = false;
    private Long sessionStartTime;
    private static SpriteSheets spriteSheets;
    private int pastHour = 0;
    private static Server server;

    /**
     * The constructor initialises the {@link GlobalVariables},
     * grabbing everything from the configuration file to be used thorough the application. Then it
     * creates a new {@link Socket} connection.
     * In the end it makes sure that all the sprites are loaded in memory by {@link SpriteSheets}
     */
    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour = "";
        new GlobalVariables();
        logger = LogManager.getLogger(getClass().getName());
        try {
            socket = IO.socket(GlobalVariables.WEBSERVICE_SOCKET);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        instance = this;
        spriteSheets = new SpriteSheets();
        spriteSheets.load();
        server = new Server(GlobalVariables.SERVER_LISTEN_PORT);
        server.start();
    }

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     * In here we change the theme to default one (Which is light
     *
     * @since 1.0.0
     */
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

    /**
     * Prepare all the socket events
     *
     * @since 1.0.1
     */
    private void socketPreparation() {
        socket.on(Socket.EVENT_CONNECT, args_cn -> {
            logger.info("Connected to the web server. Authorising...");
            popup.show("Connected! Authorising...");
            disconnectedUI(true);
            socket.emit("authorisation", GlobalVariables.CAR_PARK_ID, GlobalVariables.CAR_PARK_NAME, (Ack) objects -> {
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
                    Platform.runLater((() -> Alerter.showUnableToStartAlert(
                            "Unauthorised access!",
                            "You are not authorised to access the web service. " +
                                    "Please make sure the configuration details given by your administrator are correct and try again."
                    )));

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
        socket.on(Socket.EVENT_DISCONNECT, args_dc -> {
            logger.error("Disconnected");
            disconnectedUI(true);
            sceneManager.changeTo(Scenes.LANDING);
            popup.show("Disconnected");
        });
        socket.connect();
    }

    /**
     * Method that gives the instance of this class
     *
     * @return the class instance
     * @since 1.0.1
     */
    public static MainViewController getInstance() {
        return instance;
    }

    /**
     * Method that returns the current socket connection
     *
     * @return the current socket
     * @since 1.0.1
     */
    public Socket getSocket() {
        return socket;
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

    /**
     * Set the ui in a disabled state, unable to interact with
     *
     * @param enabled whether or not to activate this state
     * @since 1.0.1
     */
    private void disconnectedUI(boolean enabled) {
        sceneAnchor.setDisable(enabled);
    }

    /**
     * This method starts a thread that changes the scene to the landing page when
     * no interaction with the ui happened for a defined time
     *
     * @since 1.0.3
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
     * Thread that updates the date and time,
     * also switches the themes if auto nightime is turned on
     *
     * @since 1.0.1
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

    /**
     * Method for the switch themes button that switches ... the themes (self explanatory)
     *
     * @since 1.0.4
     */
    @FXML
    void switchTheme() {
        if (ThemeProvider.getInstance().getCurrentTheme() == Themes.LIGHT) {
            ThemeProvider.getInstance().switchTheme(Themes.DARK);
        } else {
            ThemeProvider.getInstance().switchTheme(Themes.LIGHT);
        }
        updateThemeButton();
    }

    /**
     * Method that updates the switch themes button from the current theme
     *
     * @since 1.0.4
     */
    private void updateThemeButton() {
        if (ThemeProvider.getInstance().getCurrentTheme() == Themes.DARK) {
            themeModeButton.setText("DAYTIME");
        } else {
            themeModeButton.setText("NIGHTTIME");
        }
    }

    /**
     * Get the running server instance
     * @return the running server
     */
    public  Server getServer() {
        return server;
    }

    /**
     * Sends ticket generation request to  the server. Once a response has been received,
     * the method {@link #receivedTicket(Object[], PrintWriter)}} will be called with corresponding info
     *
     * @since 1.0.6
     */
    public void requestTicket(PrintWriter prin) {
        socket.emit("request-ticket", (Ack) objects -> receivedTicket(objects, prin));
    }

    /**
     * Method called once a response has been received from the server
     *
     * @param objects the objects received from the server
     *                (index 0 contains the error code,
     *                index 1 contains either the ticket or info message)
     */
    public void receivedTicket(Object[] objects, PrintWriter print) {
        System.out.println(objects[1]);
        print.println((objects[1]));
    }

    /**
     * Sends a request to the server to verify whether or not a ticket is valid for exit.
     * This means the ticket has to exist, be valid and paid. Once these criteria are met, the ticket gets invalidated
     *
     * @param ticketID the ticket id to check
     */
    public void requestTicketValidity(String ticketID, PrintWriter print) {
        socket.emit("ticket-exit", ticketID, (Ack) objects -> {
            logger.info("Ticket exit request: " + ticketID + " Response:  " + objects[0] + " " + objects[1]);
            if (objects[0].equals(200)) {
                receivedRequestResponse(true,print);
            } else {
                receivedRequestResponse(false,print);
            }
        });
    }

    /**
     * Sends a request to the server to verify whether or not a ticket contained in the smart card is valid for exit.
     * This means the ticket has to exist, be valid and paid. Once these criteria are met, the ticket gets invalidated
     *
     * @param smartcardID the smart ardID id to check
     */
    public void requestSmartcardValidity(String smartcardID, PrintWriter print) {
        socket.emit("smartcard-exit", smartcardID, (Ack) objects -> {
            logger.info("Smartcard exit request: " + smartcardID + " Response:  " + objects[0] + " " + objects[1]);
            if (objects[0].equals(200)) {
                receivedRequestResponse(true,print);
            } else {
                receivedRequestResponse(false,print);
            }
        });
    }

    /**
     * Verifies whether or not the smart card is valid for entrance
     *
     * @param smartcardId the smart card id ot check
     */
    public void checkSmartcard(String smartcardId,PrintWriter print) {
        socket.emit("smartcard-enter", smartcardId, (Ack) objects -> {
            logger.info("Smartcard enter request: " + smartcardId + " Response:  " + objects[0] + " " + objects[1]);
            if (objects[0].equals(200)) {
                receivedRequestResponse(true,  print);
            } else {
                receivedRequestResponse(false, print);
            }
        });
    }

    public void updatedInfoPane(){
        server.update();
    }
    public void receivedRequestResponse(boolean allowed, PrintWriter print) {
        System.out.println(allowed);
        if(allowed) print.println("True");
        else print.println("False");
    }

}
