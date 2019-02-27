package uk.co.holborn.carparkclient;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;

public class Alerter {
    private static String OS = System.getProperty("os.name").toLowerCase();
    ;

    public static void showAlert(String title, String header, String content, Alert.AlertType alertType, EventHandler<DialogEvent> onCloseEvent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Alerter.class.getResource(Themes.LIGHT.getStylesheetPath()).toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setMinHeight(200);
        dialogPane.setMinWidth(600);
        alert.setOnCloseRequest(onCloseEvent);
        alert.showAndWait();

    }

    public static void showUnableToStartAlert(String header, String content) {
        showUnableToStartAlert(header, content, null);
    }

    public static void showUnableToStartAlertAndOpenRunningDirectory(String header, String content) {
        showUnableToStartAlert(header, content, e -> {
            openRunningDirectory();
            System.exit(-1);
        });
    }

    public static void showUnableToStartAlert(String header, String content, EventHandler<DialogEvent> onCloseEvent) {
        showAlert("Unable to start", header, content, Alert.AlertType.ERROR, onCloseEvent);

    }

    public static void openRunningDirectory() {
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

    public static boolean isWindows() {

        return (OS.contains("win"));

    }

    public static boolean isMac() {

        return (OS.contains("mac"));

    }

    public static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));

    }
}
