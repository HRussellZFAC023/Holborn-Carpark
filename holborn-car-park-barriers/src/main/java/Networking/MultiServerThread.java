package Networking;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiServerThread extends Thread {

    private Socket socket;//Current socket being used for communication

    public MultiServerThread(Socket socket) {
        super("BarrierComThread");
        System.out.println("Socket made on port: " + socket.getPort());
        this.socket = socket;//Sets the socket
    }//Constructor for thread, names thread and sets the socket being used

    public void run() {
        try {
            Scanner scan = getScanner();//Get the input stream
            boolean inputOutput = new Protocol().getType(scan);//Define what type of barrier is connected
            System.out.println("Recieved: " + inputOutput);
            //Switch to the correct handling procedure for the barrier type
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
    }//Main code of the server thread, deals with running the correct protocols and ending the connection afterwards

    private void inBarrier(Scanner scan, PrintWriter print) {
        String line;
        //While the stop command has not been given
        while (!(line = waitAnswer(scan)).contentEquals("Halt")) {
            //If the get command has been received
            if (line.contentEquals("Get")) {
                Ticket ticket = new Ticket("Networking.Ticket");//Needs to generate ticket here
                //Convert the ticket to a string and send it to the barriers
                print.println((new Gson()).toJson(ticket));
            }
        }
    }//The code run by an in barrier connection to retrieve a new ticket

    private void outBarrier(Scanner scan, PrintWriter print) {
        String line;
        //While the stop command has not been given
        while (!(line = waitAnswer(scan)).contentEquals("Halt")) {
            //If the check command is sent
            if (line.contains("Check:")) {
                //Removes teh command sent by the barrier
                String ticketID = line.substring(6);
                //Need to check ticket with database here
                if (ticketID.contentEquals("Networking.Ticket")) {
                    print.println("True");
                    //Open barrier
                    //Maybe return something to let the database know the car has left
                } else {
                    print.println("False");
                    //Display error message
                }
            }
        }
    }//The code run by an out barrier connection to validate a ticket

    private String waitAnswer(Scanner scan) {
        String input = "";
        //Loop whilst there is no input
        while ((input = scan.nextLine()) == null) {
            sleep(5);//Wait 5 milliseconds
        }
        return input;
    }//Wait till there is a string to read

    private void checkStop(String line) throws IOException {
        if (line.contentEquals("Halt")) {
            System.out.println("Stopping socket with port: " + socket.getPort());
            socket.close();//Close the socket
        }
    }//Checks whether the received string is for terminating the connection

    private Scanner getScanner() throws IOException {
        return new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
    }//Gets the input stream of the socket in a scanner

    private PrintWriter getPrint() throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }//Gets the output stream of the socket in a print writer

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }//Make the thread sleep for the inputted number of milliseconds
}
