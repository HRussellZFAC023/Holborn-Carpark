package uk.co.holborn.carparkclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import uk.co.holborn.carparkclient.Animator;
import uk.co.holborn.carparkclient.Sprite;
import uk.co.holborn.carparkclient.Ticket;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentMethodsContactlessController implements Initializable {

    //    @FXML
//    Button backButton;
//    @FXML
//    TextField inputAmount;
//    @FXML
//    Label price_due;
//    @FXML
//    Label infoText;
    @FXML
    ImageView imageView;
    Ticket t;
    private MainViewController mc;
    private Sprite sprite;

    @Override

    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
       //sprite = new Sprite(imageView, mc.getSpriteSheets().getImage(Sprites.PAYMENT_APPLE_PAY), 1000, 1000);
        sprite.setSpritesCount(120);
        sprite.setFPS(30);
    }

    @FXML
    public void back() {
        mc.sceneManager.goBack();
    }

    public void setup() {
        sprite.resetView();
        Animator.nodePopIn(imageView,0.6, e->{
            sprite.replay();
        });
    }

//    public void pay() {
//        double amount;
//        try {
//            amount = Double.parseDouble(inputAmount.getText());
//        } catch (Exception e) {
//            amount = 0;
//        }
//        due -= amount;
//        paid += amount;
//        if (due <= 0.0) {
//            change = Math.abs(due);
//            due = 0.0;
//            inputAmount.setDisable(true);
//            backButton.setVisible(false);
//            if( change >0) setInfoText("Please take your change of £" + change);
//            Thread t = new Thread(() -> {
//                try {
//                    mc.emitTicketPaid();
//                    Thread.sleep(3000);
//                    mc.sceneManager.changeTo(Scenes.LANDING);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//            t.setName("Thread-Sleep");
//            t.setDaemon(true);
//            t.start();
//        }
//        inputAmount.clear();
//        updateUI();
//    }
//
//    private void updateUI() {
//        setAmoundDue("£" + due);
//        setPaidAmount("£" + paid);
//    }
//
//    private void setAmoundDue(String amount) {
//        price_due.setText(amount);
//        Animator.nodeFade(price_due, true);
//    }
//
//    private void setPaidAmount(String amount) {
//        price_paid.setText(amount);
//        Animator.nodeFade(price_paid, true);
//    }
//
//    private void setInfoText(String amount) {
//        infoText.setText(amount);
//        Animator.nodeFade(price_due, true);
//    }


}
