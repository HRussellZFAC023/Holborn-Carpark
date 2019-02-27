package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Toggle;
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
    Label price;
    @FXML
    Label amountText;
    @FXML
    ImageView imageView;
    @FXML
    VBox toggleVbox;
    ToggleGroup toggleGroup;

    Ticket t;
    private MainViewController mc;
    private Sprite sprite;
    private boolean firstTime;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        toggleGroup = new ToggleGroup();
    }
    private void setOtherTogglesDisabled(){
        for (Toggle tg: toggleGroup.getToggles()) {
            if(tg.isSelected())((ToggleButton)tg).setDisable(true);
            else ((ToggleButton)tg).setDisable(false);
        }
    }

    private void popInSprite(Sprites sp) {
        if (sprite == null) sprite = new Sprite(imageView, mc.getSpriteSheets().getSpriteSettings(sp));
        else {
            sprite.pause();
            sprite.setSpriteSettings(mc.getSpriteSheets().getSpriteSettings(sp));
        }
        if (firstTime) {
            firstTime = false;
            Animator.nodePopIn(imageView, 0.4, e -> {
                sprite.replay();
            });
        } else {
            sprite.replay();
            Animator.nodePopIn(imageView, 0.0, e -> {
//            sprite.replay();
            });
        }


    }

    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    private void configureToggles(){
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(0));
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(1));
        toggleGroup.getToggles().get(0).setUserData("Debit/Credit");
        toggleGroup.getToggles().get(1).setUserData("Apple Pay");
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if(toggleGroup.getSelectedToggle()!= null){
                if (toggleGroup.getSelectedToggle().getUserData().equals("Apple Pay")) {
                    popInSprite(Sprites.PAYMENT_APPLE_PAY);
                } else {
                    popInSprite(Sprites.PAYMENT_CARD_CONTACTLESS);
                }
                setOtherTogglesDisabled();
            }
        });
    }
    public void setup() {
        firstTime = true;
        configureToggles();
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));

        if (mc.ticket.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            amountText.setText("Amount remaining");
        } else {
            amountText.setText("Fee");
        }
        price.setText("£" + (mc.ticket.getPrice().subtract(mc.ticket.getAmountPaid())));
    }

}
