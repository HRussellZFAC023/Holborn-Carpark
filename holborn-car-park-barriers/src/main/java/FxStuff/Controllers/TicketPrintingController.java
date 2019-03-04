package FxStuff.Controllers;

import FxStuff.Ticket;
import QRCode.QRCode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public TicketPrintingController(MainViewController mainCont) {
        Logger logger = LogManager.getLogger(getClass().getName());
        this.mainCont = mainCont;
    }

    /**
     * This method sets a thread that changes the scene back to the landing page after the
     * ticket has been received and printed(here, made into a PDF)
     *
     * @since 1.0.0
     */
    private void goBackAfterPrinting() {
        Thread waitPrint = new Thread(() -> {
            Ticket newTicket = mainCont.getNewTicket();
            if (newTicket == null) {
                System.out.println("Failed to get ticket");
            } else {
                Platform.runLater(() ->printTicket(newTicket.get_id()));
                System.out.println("Ticket: " + newTicket.toString());
            }
            mainCont.getSceneManager().goBack();
        });
        //TODO PDF thingy
        waitPrint.setName("WaitForPrintThread");
        waitPrint.setDaemon(true);
        waitPrint.start();
    }

    private void printTicket(String ID) {
        Document ticket = new Document();
        try {
            QRCode qrGen = new QRCode();
            String location = qrGen.generate(ID, 3, 10);
            Path path = Paths.get(location);
            int pos = 0;
            while(new File(location).exists()){
                pos++;
                location = "Tickets/Ticket/Tickets/QR" + pos + ".png";
            }
            PdfWriter.getInstance(ticket, new FileOutputStream("Tickets/Ticket/Ticket" + pos + ".pdf"));
            ticket.open();

            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk chunk = new Chunk(ID, font);
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            ticket.add(img);

            ticket.add(chunk);
            ticket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method runs a thread to wait for a ticket to be received and printed before switching scenes
     *
     * @since 1.0.0
     */
    public void getTicket() {
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
