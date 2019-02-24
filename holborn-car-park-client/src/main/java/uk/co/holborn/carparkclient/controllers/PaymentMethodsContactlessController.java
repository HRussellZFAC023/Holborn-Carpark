package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Sprite;
import uk.co.holborn.carparkclient.Sprites;
import uk.co.holborn.carparkclient.Ticket;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsContactlessController implements Initializable {

    @FXML
    ImageView imageView;
    @FXML
    VBox toggleVbox;
    ToggleGroup toggleGroup;

    Ticket t;
    private MainViewController mc;
    private Sprite sprite;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(0));
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(1));
        toggleGroup.getToggles().get(0).setUserData("Debit/Credit");
        toggleGroup.getToggles().get(1).setUserData("Apple Pay");

        toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
                if (newToggle.getUserData().equals("Apple Pay")) {
                    popInSprite(Sprites.PAYMENT_APPLE_PAY);
                } else {
                    popInSprite(Sprites.PAYMENT_CARD_CONTACTLESS);
                }
                ((ToggleButton) newToggle).setDisable(true);
                if (oldToggle != null) ((ToggleButton) oldToggle).setDisable(false);
        });
    }

    private void popInSprite(Sprites sp) {
        if (sprite == null) sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(sp));
        else {
            sprite.pause();
            sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(sp));
        }
        Animator.nodePopIn(imageView, 0.4, e -> {
            sprite.replay();
        });

    }

    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    public void setup() {
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
    }

}
