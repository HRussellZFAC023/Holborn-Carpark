package FxStuff;

import FxStuff.Controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Parent root;
        root = FXMLLoader.load(getClass().getResource("/fxml/main_view.fxml"));
        mainStage.setTitle(GlobalVariables.main_window_name);
        mainStage.setFullScreen(false);
        //mainStage.setFullScreen(true);
        mainStage.setFullScreenExitHint("");
        mainStage.setScene(new Scene(root, 1280, 768));
        mainStage.setMinHeight(400);
        mainStage.setMinWidth(600);
        mainStage.show();
    }

    @Override
    public void stop(){
        MainViewController.getInstance().disconnect();
        //Add method to close the sockets
    }
}
