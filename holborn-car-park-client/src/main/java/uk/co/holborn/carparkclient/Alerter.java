package uk.co.holborn.carparkclient;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Alerter class implements easy ways to alert user of different behaviour
 * *
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class Alerter {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Show a customised alert
     *
     * @param title        the title of the alert
     * @param header       the header of the alert
     * @param content      the information displayed
     * @param alertType    the type of the alert
     * @param onCloseEvent event triggered once the alert gets closed
     */
    private static void showAlert(String title, String header, String content, Alert.AlertType alertType, EventHandler<DialogEvent> onCloseEvent) {
        Alert a = getAlert(title, header, content, alertType);
        a.setOnCloseRequest(onCloseEvent);
        a.showAndWait();

    }

    /**
     * Get a new predefined alert model
     *
     * @param title     the title of the alert
     * @param header    the header of the alert
     * @param content   the information displayed
     * @param alertType the type of the alert
     * @return the alert
     */
    private static Alert getAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Alerter.class.getResource(Themes.LIGHT.getStylesheetPath()).toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setMinHeight(200);
        dialogPane.setMinWidth(600);
        return alert;
    }

    /**
     * Display an unable to start alert that the app can't run because of the given reason
     * and opens the running directory for configuration on a button prompt
     *
     * @param header  the header of the alert
     * @param content the information displayed
     */
    public static void showUnableToStartAlertAndOpenRunningDirectory(String header, String content) {
        ButtonType showLocation = new ButtonType("Show Location", ButtonBar.ButtonData.LEFT);
        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = getAlert("Unable to start!", header, content, Alert.AlertType.ERROR);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(showLocation, close);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == showLocation) {
            openRunningDirectory();
            System.exit(-1);
        } else {
            System.exit(-1);
        }
    }

    /**
     * Show an unable to start alert
     *
     * @param header  the header of the alert
     * @param content the information displayed
     */
    public static void showUnableToStartAlert(String header, String content) {
        showAlert("Unable to start", header, content, Alert.AlertType.ERROR, t -> System.exit(-1));

    }

    /**
     * This method opens the the system's file explorer in the application's running directory
     * <p>On mac, it opens Finder.</p>
     * <p>On windows, it opens File Explorer</p>
     * <p>On linux, it opens the default file explorer/p>
     */
    private static void openRunningDirectory() {
        File currentJavaJarFile = new File(Alerter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
        String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
        try {
            if (isMac()) Runtime.getRuntime().exec("open " + currentRootDirectoryPath);
            if (isUnix()) Runtime.getRuntime().exec("xdg-open " + currentRootDirectoryPath);
            if (isWindows()) Runtime.getRuntime().exec("explorer.exe " + currentRootDirectoryPath);
        } catch (IOException e) {
            LogManager.getLogger(Alerter.class.getName()).error(e.getMessage());
        }
    }

    /**
     * Method that says whether or not the current platform is Windows
     * @return true or false
     */
    private static boolean isWindows() {

        return (OS.contains("win"));

    }
    /**
     * Method that says whether or not the current platform is MacOS
     * @return true or false
     */
    private static boolean isMac() {

        return (OS.contains("mac"));

    }

    /**
     * Method that says whether or not the current platform is Unix based system
     * @return true or false
     */
    private static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));

    }
}
