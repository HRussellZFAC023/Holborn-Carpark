package Networking;

import FxStuff.Controllers.LandingInPageController;
import FxStuff.Controllers.MainViewController;
import FxStuff.GlobalVariables;
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
    private boolean connected = false;
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
        InfoPopUp popup = mainCont.getPopup();
        Logger logger = mainCont.getLogger();
        popup.show("Connecting...");
        mainCont.disconnectedUI(true);
        String[] socketDefinitions = mainCont.getSocket();
        String hostname = socketDefinitions[0];
        int portNumber = Integer.parseInt(socketDefinitions[1]);
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
        authorise(popup, logger);
        if (GlobalVariables.getBarrierType()) {
            LandingInPageController lc = (LandingInPageController) Scenes.LANDING_IN.getController();
            Platform.runLater(lc::enableFetching);
        }
        mainCont.disconnectedUI(false);
        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
    }//Connect to the server and define what type of connection is being created

    public Ticket getTicket() {
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
        } catch (NoConnectionError nc) {
            disconnected();
            ticket = getTicket();
        }
        return ticket;
    }//Request a ticket from the server

    public boolean validateTicket(Ticket ticket) {
        try {
            //Validate the ticket using the protocol
            return new Protocol().validate(ticket.get_id(), getScanner(), getPrinter());
        } catch (IOException e) {
            disconnected();
            //e.printStackTrace();
            return validateTicket(ticket);
        }
    }//Check with the server if the ticket is valid

    public void endConnection() {
        try {
            //Send the closing command to the server
            getPrinter().println("Halt");
            //Close the socket
            socket.close();
            connected = false;
        } catch (IOException e) {

        }
    }//Terminate the connection to the server and close the socket

    private Scanner getScanner() throws IOException {
        return new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
    }//Get the input stream of the socket in a scanner

    private PrintWriter getPrinter() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }//Get the output stream of the socket in a print writer

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

    private void disconnected() {
        connected = false;
        InfoPopUp popup = mainCont.getPopup();
        mainCont.disconnectedUI(true);
        popup.show("Disconnected");
        sleep(500);
        mainCont.disconnectedUI(true);
        reconnect(popup);
    }

    private void reconnect(InfoPopUp popup) {
        popup.show("Reconnecting...");
        Logger logger = mainCont.getLogger();
        String[] socketDefinitions = mainCont.getSocket();
        String hostname = socketDefinitions[0];
        int portNumber = Integer.parseInt(socketDefinitions[1]);
        while (!connected) {
            sleep(100);
            try {
                socket = new Socket(hostname, portNumber);
                getPrinter().println(mainCont.getBarrier_type());//Send the type of barrier to the server
                connected = true;
            } catch (IOException e) {
                System.out.print("\rUnable to reconnect to: " + hostname + ":" + portNumber + ". Attempting to reconnect...");
                //e.printStackTrace();
            }
        }
        logger.info("Reconnected to the web server. Authorising...");
        authorise(popup, logger);
        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
    }

    private void authorise(InfoPopUp popup, Logger logger) {
        popup.show("Connected! Authorising...");
        mainCont.disconnectedUI(true);
        sleep(500);//Add an authorisation method, might not be needed.
        popup.show("Authorised", false);
        popup.removePopUp();
        logger.info("Authorised!");
        mainCont.disconnectedUI(false);
    }

    public Object[] getCarparkDetails() {
        Object[] details;
        try {
            PrintWriter out = getPrinter();//Get the input and output streams
            Scanner scan = getScanner();
            //Get the string version of the ticket object
            String detailsString = new Protocol().update(scan, out);
            //Convert the string to a ticket
            details = new Gson().fromJson(detailsString, Object[].class);
        } catch (IOException e) {
            disconnected();
            details = getCarparkDetails();
            //e.printStackTrace();
        }
        return details;
    }

    public boolean isConnected() {
        return connected;
    }
}
