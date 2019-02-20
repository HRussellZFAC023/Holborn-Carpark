package Networking;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String ip = "127.0.0.1";//Needs to be changed
    private String port = "4444";//The port that the barriers connect to
    private Socket socket;

    public Client(boolean type) {
        connect(type);//Connect to the server
        //Below is testing code for an input barrier
        Ticket ticket = getTicket();//Request ticket
        if (ticket != null) {
            System.out.println("Recieved ticket with ID: " + ticket.getID());
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }
            //Print ticket
        }
        endConnection();//End connection with server
        System.out.println("Ended " + (type ? "Input" : "Output") + " thread.");
    }//Constructor for test in barrier

    public Client(boolean type, Ticket ticket) {
        connect(type);//Connect to server
        System.out.println("Networking.Ticket is: " + validateTicket(ticket));//Check if ticket is valid
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
        endConnection();//End connection to server
        System.out.println("Ended " + (type ? "Input" : "Output") + " thread.");
    }//Constructor for test out barrier

    public void connect(boolean input) {
        //Set the variables for the socket definition in the try, catch
        String hostname = ip;
        int portNumber = Integer.parseInt(port);
        try {
            socket = new Socket(hostname, portNumber);//Create the socket
            System.out.println((input ? "Input" : "Output") + " thread connecting.");
            getPrinter().println(input);//Send the type of barrier to the server
        } catch (IOException e) {
            System.err.println("Error with " + (input ? "Input" : "Output") + " thread.");
            e.printStackTrace();
        }
    }//Connect to the server and define what type of connection is being created

    public Ticket getTicket() {
        if (socket == null) {//Return null if not connected
            return null;
        }
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
        if (socket == null) {//If there is no connection return null
            return false;
        }
        try {
            //Validate the ticket using the protocol
            return new Protocol().validate(ticket.getID(), getScanner(), getPrinter());
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
}
