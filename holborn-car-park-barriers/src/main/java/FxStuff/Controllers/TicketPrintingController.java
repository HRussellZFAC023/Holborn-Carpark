package FxStuff.Controllers;

import FxStuff.Ticket;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls the ticket printing screen.
 *
 * @author Cameron
 * @version 1.0.0
 */
public class TicketPrintingController implements Initializable {

    private MainViewController mainCont;

    /**
     * Constructor that passes in the mainViewController for referencing
     *
     * @param mainCont The mainViewController
     * @since 1.0.0
     */
    public TicketPrintingController(MainViewController mainCont){
        Logger logger = LogManager.getLogger(getClass().getName());
        this.mainCont = mainCont;
    }

    /**
     * This method sets a thread that changes the scene back to the landing page after the
     * ticket has been received and printed(here, made into a PDF)
     *
     * @since 1.0.0
     */
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
        });
        //TODO PDF thingy
        waitPrint.setName("WaitForPrintThread");
        waitPrint.setDaemon(true);
        waitPrint.start();
    }

    private void printTicket(){

    }

    /**
     * This method runs a thread to wait for a ticket to be received and printed before switching scenes
     *
     * @since 1.0.0
     */
    public void getTicket(){
        goBackAfterPrinting();
    }

    /**
     * Method that prepares the UI
     *
     * @since 1.0.0
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
