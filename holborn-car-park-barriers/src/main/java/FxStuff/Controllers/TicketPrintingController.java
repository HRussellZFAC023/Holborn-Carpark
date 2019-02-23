package FxStuff.Controllers;

import FxStuff.Scenes;
import FxStuff.Ticket;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketPrintingController implements Initializable {

    private MainViewController mainCont;

    public TicketPrintingController(MainViewController mainCont){
        this.mainCont = mainCont;
    }

    private void goBackAfterPrinting(){
        Thread waitPrint = new Thread(()-> {
            Ticket newTicket = mainCont.getNewTicket();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            if(newTicket == null){
                System.out.println("Failed to get ticket");
            }else{
                System.out.println("Ticket: " + newTicket.toString());
            }
            mainCont.getSceneManager().goBack();
        });//Code needs to be replaced with waiting for the ticket to be printed
        waitPrint.setName("WaitForPrintThread");
        waitPrint.setDaemon(true);
        waitPrint.start();
    }

    public void getTicket(){
        goBackAfterPrinting();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
