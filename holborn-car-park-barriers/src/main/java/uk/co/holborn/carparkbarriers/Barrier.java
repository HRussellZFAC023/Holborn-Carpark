package uk.co.holborn.carparkbarriers;

import uk.co.holborn.carparkbarriers.Controllers.LandingInPageController;
import uk.co.holborn.carparkbarriers.Controllers.MainViewController;
import com.google.gson.Gson;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that controls some of the communications between the client and the barriers.
 *
 * @author Cameron
 * @version 1.0.0
 */
public class Barrier extends Thread {

    //TODO Convert sout's into logger usages

    private Socket socket;//The socket that the barrier is connected to
    private boolean connected = false;//Whether the socket is connected to teh server or not
    private MainViewController mainCont;
    //A reference to the mainView controller so that the UI can be manipulates if the connection to the server is lost
    private boolean handling = false;//Boolean to stop concurrency issues
    private boolean running = true;//Boolean that keeps the barrier listening for communication from the client

    /**
     * The class constructor, sets the mainViewController reference.
     *
     * @param mainCont A refrence to the main controller so the class can manipulate the UI in teh event of a socket disconnect.
     * @since 1.0.0
     */
    public Barrier(MainViewController mainCont) {
        this.mainCont = mainCont;
    }//Constructor for barrier

    /**
     * The main method of the thread, connects the socket to the client then
     * listens for communications from the client.
     *
     * @since 1.0.0
     */
    @Override
    public void run() {
        connect();//Connects to the clients socket.
        listenForServer();//Listens for commands from the client until the connection is closed
    }//Main method for the thread

    /**
     * Method that listens to teh communications from the server for commands to run:
     * 1) Halt: Initiates the termination of the connection to the socket.
     * 2) Update: Makes the socket request an update of the carpark details from the client.
     *
     * @since 1.0.0
     */
    private void listenForServer() {
        while (running) {//While the barrier is running
            if (!handling) {//If no commands are being executed
                try {
                    String input;
                    if ((input = getScanner().nextLine()) != null) {//Wait for an input
                        switch (input) {//Switch case to process the command received, if any
                            case ("Halt"):
                                endConnection();//Initiate the termination of the connection to the client
                                socket.close();//Close the socket
                                break;
                            case ("Update")://Request an update of the carpark details from the client
                                getCarparkDetails();
                                break;
                        }
                    }
                } catch (Exception ignore) {
                    disconnected();//Reconnect if disconnected
                }
            } else {
                waitForPriority();//Wait for priority to listen for commands
            }
        }
    }//Method to process commands from the server

    /**
     * Method that connects to the clients socket, receives the socket from the global variables
     *
     * @since 1.0.0
     */
    public void connect() {
        //TODO Ask Vlad about the logger use here
        InfoPopUp popup = mainCont.getPopup();//Get a reference to the info popup so that it can be manipulated
        Logger logger = LogManager.getLogger(getClass().getName());
        popup.show("Connecting...");//Set the popup to show the connecting message
        mainCont.disconnectedUI(true);//Set the UI to be uneditable
        String[] socketDefinitions = mainCont.getSocket();//Get the socket definitions from the globals class
        String hostname = socketDefinitions[0];//Set the hostname to the first definition
        int portNumber = Integer.parseInt(socketDefinitions[1]);//Set the port to the second definition
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        while (!connected) {//While the socket is not connected
            sleep(100);//Wait so that other processes can be executed even if there are connection problems
            try {
                socket = new Socket(hostname, portNumber);//Create the socket
                getPrinter().println(mainCont.getBarrier_type());//Send the type of barrier to the server
                connected = true;//Set connected to true so that the loop can be broken
            } catch (IOException e) {
                System.out.print("\rUnable to connect to: " + hostname + ":" + portNumber + ". Attempting to reconnect...");
                //e.printStackTrace();
            }
        }
        logger.info("Connected to the web server. Authorising...");
        authorise(popup, logger);//Run the authorisation method
        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
    }//Connect to the server and define what type of connection is being created

    /**
     * Method that retrieves a ticket from the client.
     *
     * @return A new ticket from the database.
     * @since 1.0.0
     */
    public Ticket getTicket() {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        Ticket ticket = null;//Create a variable to store the ticket in and set it to null
        try {
            if (socket.isClosed()) {
                throw new NoConnectionError("Not connected.");
            }
            PrintWriter out = getPrinter();//Get the input and output streams
            Scanner scan = getScanner();
            //Get the string version of the ticket object
            String tickString = new Protocol().requestTicket(scan, out);
            //Convert the string to a ticket
            if (tickString != null) {
                ticket = new Gson().fromJson(tickString, Ticket.class);


            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoConnectionError nc) {
            handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
            disconnected();//Reconnect if connection is lost
            ticket = getTicket();//Re-attempt to get a ticket after connection is resumed
        }
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
        return ticket;
    }//Request a ticket from the server

    /**
     * Method that checks with the client whether the scanned ticket is paid.
     *
     * @param ID The ID of the ticket
     * @return true if the ticket is paid, false otherwise
     * @since 1.0.0
     */
    public boolean checkTicket(String ID) {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        boolean valid = false;//Set the validity of the ticket to false
        try {
            //Validate the ticket using the protocol
            if (socket.isClosed()) {
                throw new NoConnectionError("Not connected.");
            }
            valid = new Protocol().checkTicket(ID, getScanner(), getPrinter(), false);
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (NoConnectionError nce) {
            handling = false;
            disconnected();//Attempt to reconnect to the client
            //e.printStackTrace();
            valid = checkTicket(ID);//Retry validating the ticket
        }
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
        return valid;
    }//Check with the server if the ticket is valid

    /**
     * Method that checks with the client whether the scanned smartcard is valid.
     *
     * @param ID The smartcard's ID
     * @return true if the smartcard is valid, false otherwise
     * @since 1.0.0
     */
    public boolean validateSmartcard(String ID) {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        boolean valid = false;//Set the validity of the ticket to false
        try {
            if (socket.isClosed()) {
                throw new NoConnectionError("Not connected.");
            }
            //Validate the smartcard using the protocol
            valid = new Protocol().validateSmartCard(ID, getScanner(), getPrinter(), false);
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (NoConnectionError nce) {
            handling = false;
            disconnected();//Attempt to reconnect to the client
            //e.printStackTrace();
            valid = validateSmartcard(ID);//Retry validating the smartcard
        }
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
        return valid;
    }//Check with the server if the ticket is valid

    /**
     * Method that terminates the connection with the client.
     *
     * @since 1.0.0
     */
    public void endConnection() {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        running = false;//Set running to false so that the main thread stops listening to the client
        try {
            //Send the closing command to the server
            getPrinter().println("Halt");
            connected = false;//Set connected to false
        } catch (IOException e) {
            System.out.println("Error whilst closing socket connection.");
        }
        handling = false;//Set the concurrency boolean to false setting any held threads free
    }//Terminate the connection to the server and close the socket

    /**
     * Method to retrieve a scanner object linked to the output stream of the client.
     *
     * @return The scanner object linked to the clients output stream.
     * @since 1.0.0
     */
    private Scanner getScanner() throws IOException {
        return new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
    }//Get the input stream of the socket in a scanner

    /**
     * Method to retrieve a PrintWriter object linked to the input stream of the client.
     *
     * @return The PrintWriter object linked to the clients input stream.
     * @since 1.0.0
     */
    private PrintWriter getPrinter() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }//Get the output stream of the socket in a print writer

    /**
     * Method to wait a random amount of milliseconds up to the inputted amount.
     *
     * @param time The maximum number of milliseconds the thread will sleep for.
     * @since 1.0.0
     */
    private void sleep(int time) {
        try {//Sleep the thread for a random amount of time, up to the inputted value.
            Thread.sleep(ThreadLocalRandom.current().nextInt(time));
        } catch (Exception e) {
            System.out.println("Thread unable to sleep.");
        }
    }//Make the thread sleep for an amount up to the inputted number of milliseconds

    /**
     * Method to control the accessibility of the UI whilst not connected to the client
     * and start the reconnection process.
     *
     * @since 1.0.0
     */
    private void disconnected() {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        connected = false;//Set connected to false
        InfoPopUp popup = mainCont.getPopup();//Get a reference to the info popup so that it can be controlled
        mainCont.disconnectedUI(true);//Set the UI as unaccessable
        popup.show("Disconnected");//Set the message on the popup to disconnected
        sleep(500);//Sleep the thread for half a second to let other processes run
        reconnect(popup);//Run the reconnection method
    }//UI control and the reconnection call

    /**
     * Method to reconnect ot the client.
     *
     * @param popup The popup on the UI so that messages can be conveyed to the user.
     * @since 1.0.0
     */
    private void reconnect(InfoPopUp popup) {
        popup.show("Reconnecting...");//Set the message on the popup to be reconnecting
        Logger logger = LogManager.getLogger(getClass().getName());
        String[] socketDefinitions = mainCont.getSocket();//Get the socket definitions from the globals class
        String hostname = socketDefinitions[0];//Set the hostname to the first definition
        int portNumber = Integer.parseInt(socketDefinitions[1]);//Set the port to the second definition
        while (!connected) {//While the socket is not connected
            sleep(100);//Wait so that other processes can be executed even if there are connection problems
            try {
                socket = new Socket(hostname, portNumber);//Create the socket
                getPrinter().println(mainCont.getBarrier_type());//Send the type of barrier to the server
                connected = true;//Set connected to true
            } catch (IOException e) {
                System.out.print("\rUnable to reconnect to: " + hostname + ":" + portNumber + ". Attempting to reconnect...");
                //e.printStackTrace();
            }
        }
        logger.info("Reconnected to the web server. Authorising...");
        authorise(popup, logger);//Run the authorisation method
        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
    }//Method to reconnect the client

    /**
     * Method to reconnect ot the client.
     *
     * @param popup  The popup on the UI so that messages can be conveyed to the user.
     * @param logger The logger to make a note of all the processes and make debugging easier in the vent of a problem
     * @since 1.0.0
     */
    private void authorise(InfoPopUp popup, Logger logger) {
        //TODO Ask Vlad if this is needed.
        popup.show("Connected! Authorising...");//Set the message on the popup
        mainCont.disconnectedUI(true);//Set the UI as unaccessable
        sleep(500);//Add an authorisation method, might not be needed.
        popup.show("Authorised", false);//Set the message on the popup and remove teh loading circle
        popup.removePopUp();//Remove the popup
        logger.info("Authorised!");
        if (GlobalVariables.getBarrierType()) {//If the barrier is an in barrier set it to retrieve the carpark details
            LandingInPageController lc = (LandingInPageController) Scenes.LANDING_IN.getController();
            Platform.runLater(lc::fetchInformation);
        }
        mainCont.disconnectedUI(false);//Allow the UI to be interfaceable
    }

    /**
     * Method to retrieve the Json with the carpark details from the client and convert it into an Object array.
     *
     * @return Returns a 4 element object array with the carpark details on it.
     * @since 1.0.0
     */
    public String[] getCarparkDetails() {
        waitForPriority();//Wait for priority to communicate with the client
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        String[] details;//Create the String array to store the information
        try {
            if (socket.isClosed()) {
                throw new NoConnectionError("Not connected.");
            }
            PrintWriter out = getPrinter();//Get the input and output streams
            Scanner scan = getScanner();
            //Get the string version of the Object array
            String detailsString = new Protocol().update(scan, out);
            //Convert the string to the Object array
            details = detailsString.split("&");
            if (details.length != 3) {
                details = new String[]{"Unavaliable", "Unavaliable", "Unavaliable"};
            }
        } catch (IOException e) {
            //return a default set of values
            details = new String[]{"Unavailable", "£99.99", "Unavailable"};
            //e.printStackTrace();
        } catch (NoConnectionError nce) {
            handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
            disconnected();//Attempt to reconnect
            details = getCarparkDetails();//Reattempt to get the carpark details
        }
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the client
        return details;
    }//Retrieve the carpark details from the client

    /**
     * Method to wait for priority to communicate with the client.
     *
     * @since 1.0.0
     */
    private void waitForPriority() {
        while (handling) {//While another method is communicating with the client
            sleep(100);//Wait up to 5 milliseconds
        }

    }//Wait for priority to communicate with the client

    /**
     * Method check if the barrier is connected to the client.
     *
     * @return true if the barrier is connected to the client, false otherwise.
     * @since 1.0.0
     */
    public boolean isConnected() {
        return connected;
    }//Get whether the barrier is connected to the client
}
