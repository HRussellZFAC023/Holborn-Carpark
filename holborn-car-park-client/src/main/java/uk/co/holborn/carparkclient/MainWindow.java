package uk.co.holborn.carparkclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    private Logger log;

    /**
     * The start methods prepares the UI to be shown
     *
     * @param primaryStage the stage that will be shown
     * @throws Exception exception on loading the fxml file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        log = LogManager.getLogger(getClass().getName());
        log.info("-----------Application start------------");
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            log.trace("Handler caught exception: ");
            for (int i = 0; i < throwable.getStackTrace().length; i++) {
                log.error(throwable.getStackTrace()[i]);
            }
        });

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_view.fxml"));
        primaryStage.setTitle(GlobalVariables.MAIN_WINDOW_NAME);
        primaryStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("/client_icon.png")));
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        Scene scene = new Scene(root, 1280, 768);
        new ThemeProvider(scene, Themes.LIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.show();
    }

    /**
     * This method gets called when the application stops
     *
     */
    @Override
    public void stop() {
        MainViewController mc = MainViewController.getInstance();
        log.info("-----------Application end------------");
        System.exit(0);
        mc.getSocket().close();
    }

    /**
     * Launching the app
     */
    public static void main(String[] args) {
        launch(args);
    }
}
