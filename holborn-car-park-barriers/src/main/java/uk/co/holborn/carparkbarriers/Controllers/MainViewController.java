package uk.co.holborn.carparkbarriers.Controllers;

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
import uk.co.holborn.carparkbarriers.*;
import uk.co.holborn.carparkbarriers.Sprites.SpriteSheets;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * The main view controller is the most important controller of them all.
 * It controls everything from the {@link SceneManager} to the {@link Networking} connections
 *
 * @author Vlad Alboiu
 * @author Cameron
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

    private SceneManager sceneManager;
    private Barrier barrier;
    private InfoPopUp popup;
    public String hourly_price;
    public String parking_spaces;
    public String happy_hour;
    private SpriteSheets spriteSheets;

    private int pastHour = 0;
    private Long sessionStartTime;

    /**
     * The constructor initialises the {@link GlobalVariables},
     * grabbing everything from the configuration file to be used thorough the application. Then it
     * creates a new {@link Networking} connection.
     * In the end it makes sure that all the sprites are loaded in memory by {@link SpriteSheets}
     */
    public MainViewController() {
        hourly_price = "";
        parking_spaces = "";
        happy_hour = "";
        new GlobalVariables();
        Logger logger = LogManager.getLogger(getClass().getName());
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

    /**
     * This method gets called after all the constructors have
     * done their work to prepare the ui before displaying it.
     * In here we change the theme to default one (Which is light)
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updater();
        themeModeButton.setText("NIGHTTIME");
        popup = new InfoPopUp(sceneContainer);
        sceneManager = new SceneManager(sceneAnchor);
        sceneManager.changeTo(GlobalVariables.getBarrierType()? Scenes.LANDING_IN:Scenes.LANDING_OUT);
        sceneAnchor.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> sessionStartTime = System.currentTimeMillis());
        Platform.runLater(this::serverConnection);
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
     * Set the ui in a disabled state, unable to interact with
     *
     * @param enabled whether or not to activate this state
     * @since 1.0.1
     */
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

    /**
     * This method starts a thread that changes the scene to the landing page when
     * no interaction with the ui happened for a defined time
     *
     * @since 1.0.3
     */
    void startSession() {
        sessionStartTime = System.currentTimeMillis();
        int session_timeout_ms = GlobalVariables.SESSION_TIMEOUT_S() * 1000;
        int session_timeout_popup_ms = GlobalVariables.SESSION_TIMEOUT_POPUP_DURATION_S() * 1000;
        Thread session = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (sceneManager.getCurrentScene() == (GlobalVariables.getBarrierType()?Scenes.LANDING_IN:Scenes.LANDING_OUT) || sceneManager.getCurrentScene() == Scenes.FINISH) {
                    Thread.currentThread().interrupt();
                }
                if ((System.currentTimeMillis()) - sessionStartTime >= session_timeout_ms) {
                    popup.show("Session timed out", false);
                    sceneAnchor.setDisable(true);
                    sceneManager.changeTo(GlobalVariables.getBarrierType()?Scenes.LANDING_IN:Scenes.LANDING_OUT);
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

                if (GlobalVariables.AUTO_NIGHT_TIME())
                    if (pastHour != hour) {
                        pastHour = hour;
                        boolean sameDay = GlobalVariables.NIGHT_TIME_START() < GlobalVariables.NIGHT_TIME_END();
                        boolean hourBetweenSameDay = hour >= GlobalVariables.NIGHT_TIME_START() && hour <= GlobalVariables.NIGHT_TIME_END();
                        boolean hourBetweenSameAndNextDay = hour >= GlobalVariables.NIGHT_TIME_START() || hour < GlobalVariables.NIGHT_TIME_END();
                        boolean expr = sameDay ? hourBetweenSameDay : hourBetweenSameAndNextDay;
                        if (expr) {
                            ThemeProvider.getInstance().switchTheme(Themes.DARK);
                        } else {
                            ThemeProvider.getInstance().switchTheme(Themes.LIGHT);
                        }
                        updateThemeButton();
                    }
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
     * Method that checks the inputted ID with teh client for validity
     *
     * @param ID The ID of the ticket/smartcard
     * @param smartcard Whether the ID is related to a ticket or smartcard
     *
     * @return true if the ID is valid and the barrier can be opened, false otherwise
     * @since 1.0.0
     */
    public boolean check(String ID, boolean smartcard){
        return (smartcard? barrier.validateSmartcard(ID) : barrier.checkTicket(ID));
    }

    /**
     * Method that gets the scene-manager
     *
     * @return reference to the scene-manager
     * @since 1.0.4
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Method that returns the socket definitions
     *
     * @return socket IP and port as a String array
     * @since 1.0.1
     */
    public String[] getSocket() {
        return GlobalVariables.SOCKET_ADDRESS.split(":");
    }

    /**
     * Method that returns type of barrier as defined in the config file
     *
     * @return the barriers definition as in the config file
     * @since 1.0.0
     */
    public String getBarrier_type() {
        return GlobalVariables.BARRIER_TYPE;
    }

    /**
     * Method that returns the info-popup class
     *
     * @return reference to the info-popup class
     * @since 1.0.0
     */
    public InfoPopUp getPopup() {
        return popup;
    }

    /**
     * Method that returns an array of the carpark details from the client
     *
     * @return 3 element string array
     * @since 1.0.0
     */
    public String[] getCarparkDetails() {
        return barrier.getCarparkDetails();
    }

    /**
     * Method that returns a ticket from teh client
     *
     * @return a new ticket
     * @since 1.0.0
     */
    public Ticket getNewTicket() {
        return barrier.getTicket();
    }

    /**
     * Method that disconnects the barrier from the client
     *
     * @since 1.0.0
     */
    public void disconnect() {
        if (barrier.isConnected()) {
            barrier.endConnection();
        }
    }
}
