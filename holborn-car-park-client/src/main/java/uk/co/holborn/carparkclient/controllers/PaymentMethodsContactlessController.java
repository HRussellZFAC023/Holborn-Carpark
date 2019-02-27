package uk.co.holborn.carparkclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Sprite;
import uk.co.holborn.carparkclient.Sprites;

import java.math.BigDecimal;
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
    Label infoText;
    @FXML
    VBox toggleVbox;
    @FXML
    ImageView imageValidate;
    ToggleGroup toggleGroup;

    private MainViewController mc;
    private Sprite sprite;
    private boolean firstTime;
    Thread transactionThread;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        toggleGroup = new ToggleGroup();
    }

    private void setTogglesDisabled() {
        for (Toggle tg : toggleGroup.getToggles()) {
            if (tg.isSelected()) ((ToggleButton) tg).setDisable(true);
            else ((ToggleButton) tg).setDisable(false);
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

    private void configureToggles() {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(0));
        toggleGroup.getToggles().add((ToggleButton) toggleVbox.getChildren().get(1));
        toggleGroup.getToggles().get(0).setUserData("Debit/Credit");
        toggleGroup.getToggles().get(1).setUserData("Apple Pay");
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (toggleGroup.getSelectedToggle() != null) {
                imageValidate.setOpacity(0);
                if (toggleGroup.getSelectedToggle().getUserData().equals("Apple Pay")) {
                    popInSprite(Sprites.PAYMENT_APPLE_PAY);
                    setMessage("Please bring your device near the reader");
                } else {
                    popInSprite(Sprites.PAYMENT_CARD_CONTACTLESS);
                    setMessage("Please bring your debit/credit card near the reader");
                }
                setTogglesDisabled();
            }
        });
    }

    private void verifyTransaction() {
        ///TODO <- HARDWARE MISSING: verify the card or apple pay transaction through the nfc reader, get response back
    }

    private void fakeTransaction(boolean approved) {
        if (transactionThread != null) transactionThread.interrupt();
        transactionThread = new Thread(() -> {
            try {
                imageValidate.setOpacity(0);
                setMessage("Please wait...");
                toggleVbox.setDisable(true);
                spritePushOut();
                Thread.sleep(2000);
                if (approved) {
                    animateImageValidate(true);
                    setMessage("Transaction Approved!");
                } else {
                    animateImageValidate(false);
                    setMessage("Transaction Refused! Please try again or choose another payment method");
                    toggleVbox.setDisable(false);
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
        });
        transactionThread.setName("Thread - Transaction Thread");
        transactionThread.setDaemon(true);
        transactionThread.start();
    }

    @FXML
    private void transactionApproved() {
        fakeTransaction(true);

    }

    @FXML
    private void transactionRefused() {
        fakeTransaction(false);
    }

    private void spritePushOut() {
        sprite.pause();
        Animator.nodePushOut(imageView);
    }

    private void setTogglesSelectable(boolean selectable) {

    }

    private void animateImageValidate(boolean valid) {
        imageValidate.setImage(new Image(valid ? "/img/checkmark.png" : "/img/x mark.png"));
        imageValidateShow(true);
    }

    private void imageValidateShow(boolean show) {
        if (show) Animator.nodePopIn(imageValidate);
        else Animator.nodePushOut(imageValidate);
    }

    public void setup() {
        firstTime = true;
        imageValidate.setOpacity(0);
        toggleVbox.setDisable(false);
        configureToggles();
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));

        if (mc.ticket.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            amountText.setText("Amount remaining");
        } else {
            amountText.setText("Fee");
        }
        price.setText("£" + (mc.ticket.getPrice().subtract(mc.ticket.getAmountPaid())));
    }

    private void setMessage(String message) {
        Platform.runLater(() -> {
            infoText.setText(message);
        });
        Animator.nodeFade(infoText, true);
    }

}
