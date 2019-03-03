package Networking;

import FxStuff.Ticket;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A thread class that controls the interactions between the client and the barriers.
 *
 * @author Cameron
 * @version 1.0.0
 */
public class MultiServerThread extends Thread {

    private Socket socket;//Current socket being used for communication
    private boolean handling = false;//Boolean to stop concurrency issues
    private boolean inputBarrier;//Boolean for storing the type of barrier connected to the thread

    //TODO Change sout's to logger interaction
    //TODO Change stack trace outputs to logger interaction

    /**
     * The class constructor, setting the socket that the class connects to.
     *
     * @param socket The socket to make the connection on.
     * @since 1.0.0
     */
    public MultiServerThread(Socket socket) {
        super("BarrierComThread");//Name the thread and run the higher methods
        System.out.println("Socket made on port: " + socket.getPort());
        this.socket = socket;//Sets the socket
    }//Constructor for thread, names thread and sets the socket being used

    /**
     * The main method of the thread, interacts with the client to find the barrier type
     * then runs the associated methods with that type of barrier. After the connection is stopped
     * the method shuts down the connection to the barrier.
     *
     * @since 1.0.0
     */
    public void run() {
        try {
            Scanner scan = getScanner();//Get the input stream
            boolean inputOutput = new Protocol().getType(scan);//Define what type of barrier is connected
            System.out.println("Recieved: " + inputOutput);
            //Switch to the correct handling procedure for the barrier type
            inputBarrier = inputOutput;
            if (inputOutput) {
                inBarrier(scan, getPrint());
            } else {
                outBarrier(scan, getPrint());
            }
            //If the socket is not already disconnected, disconnect it
            if (socket.isConnected()) {
                checkStop("Halt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Socket stopped.");
    }//Main code of the server thread, deals with running the correct protocols and ending the connection afterwards

    /**
     * Closes the communication between the barrier and the client. Sends the command "Halt" to the barrier setting
     * the connection closing on the barriers side.
     *
     * @since 1.0.0
     */
    public void shutdown() {
        waitForPriority();//Wait for priority to communicate with the barrier
        handling = true;//Set the concurrency boolean to true so that no other methods use it
        try {
            getPrint().println("Halt");//Send the command "Halt" to the barrier
        } catch (Exception e) {
            System.out.println("Unable to halt connection to barrier, socket already disconnected.");
        }
        handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the barrier
    }//Method for terminating the connection between the barrier and the client

    /**
     * Sends the command to the input barriers for them to update the carpark values they store/display.
     *
     * @since 1.0.0
     */
    public void update() {
        if (inputBarrier) {//If the barrier is an input barrier(only input barriers need the information)
            waitForPriority();//Wait for priority to communicate with the barrier
            handling = true;//Set the concurrency boolean to true so that no other methods use it
            try {
                getPrint().println("Update");//Send the command "Update" to the barrier
            } catch (Exception e) {
                System.out.println("Unable to update client.");
            }
            handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the barrier
        }
    }//Method for initiating the update of the values on the barrier

    /**
     * Method for handling the interactions between the client and an 'input' barrier:
     * 1) Get: A ticket is generated and sent to the barrier.
     * 2) Update: The carpark details are retrieved and sent to the barrier.
     *
     * @param scan  The Scanner object connected to the barriers output stream.
     * @param print The PrintWriter object connected to the barriers input stream
     * @since 1.0.0
     */
    private void inBarrier(Scanner scan, PrintWriter print) throws IOException {
        String line;
        //While the command "Halt" has not been given
        while (!checkStop(line = waitAnswer(scan))) {
            waitForPriority();//Wait for priority to communicate with the barrier
            handling = true;//Set the concurrency boolean to true so that no other methods use it
            System.out.println("Recieved: " + line);
            print.println("ListenUp.");//Send a command to free the input stream for a command
            switch (line) {//Go to the appropriate method
                case ("Get")://Generate and send a ticket to the barrier
                    //TODO Retrieve ticket
                    Ticket ticket = new Ticket();//Needs to generate ticket here
                    //Convert the ticket to a string and send it to the barriers
                    print.println((new Gson()).toJson(ticket));
                    break;
                case ("Update")://Retireve the carpark details and send them to the barrier
                    System.out.println("Recieved update request");
                    //TODO Get carpark info
                    String infoStuff = "Some&2.50&Unavaliable";
                    print.println(infoStuff);
                    System.out.println("Sent update request.");
                    break;
                default://If the wrong command was sent send null back
                    System.out.println("Error in input request");
                    print.println((new Gson()).toJson(null));
                    break;
            }
            handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the barrier
        }
    }//The code run to interact with an 'in' barrier

    /**
     * Method for handling the interactions between the client and an 'output' barrier:
     *      1) Check: The inputted ticket ID is checked for validity with the database.
     *      2) Valid: The inputted smartcard ID is checked for validity with the database.
     *
     * @param scan  The Scanner object connected to the barriers output stream.
     * @param print The PrintWriter object connected to the barriers input stream
     * @since 1.0.0
     */
    private void outBarrier(Scanner scan, PrintWriter print) throws IOException {
        String line;
        //While the stop command has not been given
        while (!checkStop(line = waitAnswer(scan))) {
            waitForPriority();//Wait for priority to communicate with the barrier
            handling = true;//Set the concurrency boolean to true so that no other methods use it
            //If the check command is sent
            String command = line.substring(0, 6);
            String data = line.substring(6);//Separate the command and the data
            switch (command) {
                case ("Check:"):
                    print.println("ListenUp.");
                    //Set the barrier to a state where it will receive data.
                    //TODO Check the ticket with the database
                    //TODO change if statement to work with DB input
                    if (data.contentEquals("True")) {
                        print.println("True");//Send true back to the barrier
                        //Maybe return something to let the database know the car has left
                    } else {
                        print.println("False");//Send false to the barrier
                    }
                    break;
                case ("Valid:"):
                    print.println("ListenUp.");
                    //Set the barrier to a state where it will receive data.
                    //TODO Check with the database
                    //TODO change if statement to work with DB input
                    if (data.contentEquals("True")) {
                        print.println("True");//Send true back to the barrier
                    } else {
                        print.println("False");//Send false to the barrier
                    }
                    break;
                default://If the wrong command was sent send null back
                    System.out.println("Error in input request");
                    print.println("False");//Send false to the barrier
                    break;
            }
            //TODO Add a method to do with the barrier going up?
            handling = false;//Set the concurrency boolean to false allowing other processes to communicate with the barrier
        }
    }//The code run to interact with an 'out' barrier

    /**
     * Method that waits until there is an input value for the the thread to handle.
     *
     * @param scan The Scanner object connected to the barriers output stream.
     * @return An input from the connected barrier.
     * @since 1.0.0
     */
    private String waitAnswer(Scanner scan) {
        String input;
        //Loop whilst the input is null
        while ((input = scan.nextLine()) == null) {
            sleep(5);//Wait up to 5 milliseconds
        }
        return input;//Return the command
    }//Method to wait till there is a string to read

    /**
     * Method for checking whether the client has requested a termination fo the connection.
     *
     * @param line The received command
     * @return true if the command was "Halt", false otherwise
     * @since 1.0.0
     */
    private boolean checkStop(String line) throws IOException {
        if (line.contentEquals("Halt")) {//If the command received is "Halt"
            System.out.println("Stopping socket with port: " + socket.getPort());
            shutdown();//Run the disconnection method to halt the listening method on the barrier.
            socket.close();//Close the socket
            return true;
        }
        return false;
    }//Checks whether the received command is for terminating the connection

    /**
     * Method to retrieve a scanner object linked to the output stream of the connected barrier.
     *
     * @return The scanner object linked to the connected barriers output stream.
     * @since 1.0.0
     */
    private Scanner getScanner() throws IOException {
        return new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
    }//Gets the input stream of the socket in a scanner

    /**
     * Method to retrieve a PrintWriter object linked to the input stream of the connected barrier.
     *
     * @return The PrintWriter object linked to the connected barriers input stream.
     * @since 1.0.0
     */
    private PrintWriter getPrint() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }//Gets the output stream of the socket in a print writer

    /**
     * Method to wait for priority to communicate with the barrier.
     *
     * @since 1.0.0
     */
    private void waitForPriority() {
        while (handling) {//While another method is communicating with the barrier
            sleep(5);//Wait up to 5 milliseconds
        }
    }//Wait for priority to communicate with the barrier

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
}
