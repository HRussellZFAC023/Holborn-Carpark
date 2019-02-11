package uk.co.holborn.carparkclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import uk.co.holborn.carparkclient.controllers.MainViewController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Tests extends ApplicationTest {
public static Parent root;
public static Scene scene;
public static MainViewController mc;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle(GlobalVariables.MAIN_WINDOW_NAME);
        if(scene == null) {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/fxml/main_view.fxml"));
            root = loader.load();
            mc = loader.getController();
            scene = new Scene(root,1280, 768);
        }
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @AfterEach
    public void after() throws TimeoutException {
        mc.sceneManager.changeTo(Scenes.LANDING);
    }

    @Test
    public void testInvalidInputTicket() {
        clickOn(".button");
        sleep(500);
        write("This is a meaningless test to check if invalid response");
        FxAssert.verifyThat(".label", LabeledMatchers.hasText("Invalid ticket! Please seek assistance from a member of staff."));

    }
    @Test
     public void testValidTicket() {
        sleep(3000);
        clickOn(".button");
        sleep(500);
        write("cb62a50c-dd53-4856-8882-53aa3ae1c767");
        sleep(500);
        FxAssert.verifyThat(".label", LabeledMatchers.hasText("Your ticket is valid!"));

    }
}