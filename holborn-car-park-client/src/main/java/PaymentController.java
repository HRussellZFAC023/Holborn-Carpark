import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML Label amountPaid;
    @FXML Label amountDue;
    MainViewController mc;
    double due = 6.50;
    double paid = 0.00;
    double change = 0.00;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        updateFields();
    }

    @FXML
    public  void  add1Pound(){
        addValue(1.0);
    }
    @FXML
    public  void  add50Pence(){
        addValue(0.50);
    }
    public void addValue(double amount){
        paid+=amount;
        due -= amount;
        updateFields();
        verifyPayment();
    }
    public void verifyPayment(){
        if(due<=0) {
            change = Math.abs(due);
            System.out.println("Here's your change: " + change);
            mc.sceneManager.switchToScene("Start");
        }
    }
    public void updateFields(){
        amountPaid.setText("£" + paid);
        amountDue.setText("£" + due);
    }





}
