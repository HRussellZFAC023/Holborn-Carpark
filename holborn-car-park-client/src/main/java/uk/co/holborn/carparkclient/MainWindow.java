package uk.co.holborn.carparkclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader =  new FXMLLoader() ;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_view.fxml"));
        primaryStage.setTitle("uk.co.holborn.carparkclient.Notification Sender");
        primaryStage.setScene(new Scene(root, 1280, 768));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
