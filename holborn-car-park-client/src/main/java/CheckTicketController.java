import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.ResourceBundle;

public class CheckTicketController implements Initializable {


    @FXML
    TextField checkTicketField;
    @FXML
    Label infoText;
    @FXML
    Label timeLabel;
    @FXML
    Label priceLabel;
    @FXML
    GridPane ticketInfoPane;
    @FXML
    Button payButton;
    MainViewController mc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mc = MainViewController.getInstance();
        checkTicketField.clear();
        checkTicketField.textProperty().addListener((observable, oldValue, newValue)->{
            if(newValue.length()>=10){
                if(newValue.equals("TESTTICKET")){
                    displayInfo("Searching...");
                    displayTicketPane("12 minutes", "£6.20");

                }else{
                    displayInfo("Invalid ticket");
                }
            }else{
                setInfoTextVisible(false);
            }
        });
        setInfoTextVisible(false);
        setTicketInfoPaneVisible(false);
    }

    @FXML
    private void goToPayment(){
        mc.sceneManager.switchToScene("Payment");
    }
    @FXML
    private void back(){
        mc.sceneManager.goBack();
    }

    private void setInfoTextVisible(boolean visible){
        infoText.setVisible(visible);
    }
    private void setTicketInfoPaneVisible(boolean visible){
        ticketInfoPane.setVisible(visible);
        payButton.setVisible(visible);
    }
    private void displayTicketPane(String time, String price){
        setInfoTextVisible(false);
        timeLabel.setText(time);
        priceLabel.setText(price);
        setTicketInfoPaneVisible(true);

    }
    private void displayInfo(String message){
        setInfoTextVisible(true);
        infoText.setText(message);
    }


}
