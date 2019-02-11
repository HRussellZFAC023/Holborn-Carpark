package uk.co.holborn.carparkclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.controllers.MainViewController;

/**
 * Creates the main application stage/window
 *
 * @author Vlad Alboiu
 * @version 1.0
 */
public class MainWindow extends Application {
    Logger log;

    @Override
    public void start(Stage primaryStage) throws Exception {
        log = LogManager.getLogger(getClass().getName());
        log.info("-----------Application start------------");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_view.fxml"));
        primaryStage.setTitle(GlobalVariables.MAIN_WINDOW_NAME);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(new Scene(root, 1280, 768));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        MainViewController mc = MainViewController.getInstance();
        log.info("-----------Application end------------");
        System.exit(0);
        mc.getSocket().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
