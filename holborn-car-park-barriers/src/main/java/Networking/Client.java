package Networking;

import FxStuff.Controllers.LandingPageController;
import FxStuff.Controllers.MainViewController;
import FxStuff.InfoPopUp;
import FxStuff.Scenes;
import FxStuff.Ticket;
import com.google.gson.Gson;
import javafx.application.Platform;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Thread {

    private Socket socket;

    private MainViewController mainCont;

    public Client(MainViewController mainCont) {
        this.mainCont = mainCont;

    }//Constructor for barrier

    @Override
    public void run() {
        connect();//Change to allow loop here
        //Maybe add a queue
    }

    public void connect() {
        //Set the variables for the socket definition in the try, catch
        InfoPopUp popup =  mainCont.getPopup();
        Logger logger = mainCont.getLogger();
        popup.show("Connecting...");
        mainCont.disconnectedUI(true);
        String[] socketDefinitions = mainCont.getSocket();
        String hostname = socketDefinitions[0];
        int portNumber = Integer.parseInt(socketDefinitions[1]);
        boolean connected = false;
        while (!connected) {
            sleep(100);
            try {
                socket = new Socket(hostname, portNumber);//Create the socket
                getPrinter().println(mainCont.getBarrier_type());//Send the type of barrier to the server
                connected = true;
            } catch (IOException e) {
                System.out.print("\rUnable to connect to: " + hostname + ":" + portNumber + ". Attempting to reconnect...");
                //e.printStackTrace();
            }
        }
        logger.info("Connected to the web server. Authorising...");
        popup.show("Connected! Authorising...");
        mainCont.disconnectedUI(true);
        sleep(500);//Add an authorisation method
        popup.show("Authorised", false);
        popup.removePopUp();
        logger.info("Authorised!");
        LandingPageController lc = (LandingPageController) Scenes.LANDING.getController();
        Platform.runLater(lc::enableFetching);
        mainCont.disconnectedUI(false);

        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
    }//Connect to the server and define what type of connection is being created

    public Ticket getTicket() {
        checkConnection();
        Ticket ticket = null;
        try {
            PrintWriter out = getPrinter();//Get the input and output streams
            Scanner scan = getScanner();
            //Get the string version of the ticket object
            String tickString = new Protocol().requestTicket(scan, out);
            //Convert the string to a ticket
            ticket = new Gson().fromJson(tickString, Ticket.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticket;
    }//Request a ticket from the server

    public boolean validateTicket(Ticket ticket) {
        checkConnection();
        try {
            //Validate the ticket using the protocol
            return new Protocol().validate(ticket.get_id(), getScanner(), getPrinter());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }//Check with the server if the ticket is valid

    public void endConnection() {
        try {
            //Send the closing command to the server
            getPrinter().println("Halt");
            //Close the socket
            socket.close();
        } catch (IOException e) {

        }
    }//Terminate the connection to the server and close the socket

    private Scanner getScanner() throws IOException {
        return new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
    }//Get the input stream of the socket in a scanner

    private PrintWriter getPrinter() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }//Get the output stream of the socket in a print writer

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    private void disconected(){
        InfoPopUp popup =  mainCont.getPopup();
        mainCont.disconnectedUI(true);
        mainCont.getSceneManager().changeTo(Scenes.LANDING);
        popup.show("Disconnected");
        sleep(1000);
        popup.removePopUp();
        popup.show("Reconnecting...");
        mainCont.disconnectedUI(true);
        connect();
    }

    private void checkConnection(){
        if (socket == null) {
            System.out.println("Disconnected.");
            disconected();
        }
    }

    public Object[] getCarparkDetails(){
        System.out.println("Checking connection");
        checkConnection();
        System.out.println("Connection there.");
        Object[] details = new Object[]{"Unavaliable", 10.00, "00:00 ", "00:00 "};
        try {
            PrintWriter out = getPrinter();//Get the input and output streams
            Scanner scan = getScanner();
            //Get the string version of the ticket object
            String detailsString = new Protocol().update(scan, out);
            //Convert the string to a ticket
            details = new Gson().fromJson(detailsString, Object[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return details;
    }
}
