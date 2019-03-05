package FxStuff;

import FxStuff.Controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;

/**
 * Creates the main application stage/window
 *
 * @author Vlad Alboiu
 * @author Cameron
 * @version 1.0
 */
public class MainWindow extends Application {
    private Logger log;

    /**
     * The start methods prepares the UI to be shown
     *
     * @param mainStage the stage that will be shown
     * @throws Exception exception on loading the fxml file
     */
    @Override
    public void start(Stage mainStage) throws Exception {
        log = LogManager.getLogger(getClass().getName());
        log.info("-----------Application start------------");
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Handler caught exception: " + throwable.getMessage());
            for (int i = 0; i < throwable.getStackTrace().length; i++) {
                log.error(throwable.getStackTrace()[i]);
            }
        });
        createStorage();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_view.fxml"));
        mainStage.setTitle(GlobalVariables.MAIN_WINDOW_NAME);
        mainStage.getIcons().add(new Image(MainWindow.class.getResourceAsStream("/barrier_icon.png")));
        //mainStage.setFullScreen(true);
        mainStage.setFullScreen(false);
        mainStage.setFullScreenExitHint("");
        Scene scene = new Scene(root, 1280, 768);
        new ThemeProvider(scene, Themes.LIGHT);
        mainStage.setScene(scene);
        mainStage.setMinHeight(600);
        mainStage.setMinWidth(800);
        mainStage.show();
    }

    /**
     * A method to create the storage folders if they do not exist
     *
     */
    private void createStorage() {
        String fileLocation = "Tickets";
        File file = new File(fileLocation);
        if (!file.exists()) {
            file.mkdir();
        }
        fileLocation = "Tickets/QR";
        file = new File(fileLocation);
        if (!file.exists()) {
            file.mkdir();
        }
        fileLocation = "Tickets/Ticket";
        file = new File(fileLocation);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * This method gets called when the application stops, disconnects the sockets
     */
    @Override
    public void stop() {
        log.info("-----------Application end------------");
        MainViewController.getInstance().disconnect();
    }

    /**
     * Launching the app
     */
    public static void main(String[] args) {
        launch();
    }
}
