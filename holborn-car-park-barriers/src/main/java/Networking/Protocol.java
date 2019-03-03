package Networking;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that controls some of the communications between the client and the barriers.
 *
 * @author Cameron
 * @version 1.0.0
 */
public class Protocol {

    //TODO Make the sout's work with the logger

    /**
     * Method to get the type of barrier connected to the client.
     *
     * @return true if the barrier is an 'In' barrier, false otherwise
     * @since 1.0.0
     */
    public boolean getType(Scanner scan) {
        String line;
        //Wait for an input
        while ((line = scan.nextLine()) == null) {
            sleep(1);
        }
        return line.equalsIgnoreCase("In");
    }//Protocol for getting the type of barrier

    /**
     * Method used for requesting a new ticket to be generated by the database.
     *
     * @throws NoConnectionError Thrown to initiate reconnect to cilent
     * @param scan The Scanner object connected to the barriers output stream.
     * @param out The PrintWriter object connected to the barriers input stream
     * @return Returns a Json with the ticket in
     * @since 1.0.0
     */
    public String requestTicket(Scanner scan, PrintWriter out) throws NoConnectionError {
        try {
            String request;
            out.println("Get");//Send the get command to the server to request a ticket
            //Wait for the server to send a ticket
            while ((request = scan.nextLine()) == null) {
                sleep(1);
            }
            return request;
        } catch (Exception e) {//Throw a no connection error to cause the barrier to try and re-connect to the client
            throw new NoConnectionError("No connection to the server.");
        }
    }//Requests a Json with a ticket in it

    /**
     * Method used for checking the validity of a scanned ticket.
     *
     * @throws NoConnectionError Thrown to initiate reconnect to cilent
     * @param ID The ID of the ticket to be checked.
     * @param scan The Scanner object connected to the barriers output stream.
     * @param out The PrintWriter object connected to the barriers input stream
     * @return true if the inputted ID was valid, false otherwise.
     * @since 1.0.0
     */
    public boolean checkTicket(String ID, Scanner scan, PrintWriter out) throws NoConnectionError {
        String answer;
        try {
            //Request that the client validate the inputted ticket
            out.println("Check:" + ID);
            //Wait for an answer from the server
            while ((answer = scan.nextLine()) == null) {
                sleep(1);
            }
        } catch (Exception e) {//Throw a no connection error to cause the barrier to try and re-connect to the client
            throw new NoConnectionError("No connection to the server.");
        }
        return Boolean.parseBoolean(answer);
    }//Checks if the input ID from the barrier is a valid ticket to leave the carpark

    /**
     * Method used for checking the validity of a scanned smartcard.
     *
     * @throws NoConnectionError Thrown to initiate reconnect to cilent
     * @param ID The ID of the smartcard to be checked.
     * @param scan The Scanner object connected to the barriers output stream.
     * @param out The PrintWriter object connected to the barriers input stream
     * @return true if the inputted ID was valid, false otherwise.
     * @since 1.0.0
     */
    public boolean validateSmartCard(String ID, Scanner scan, PrintWriter out) throws NoConnectionError {
        String answer;
        try {
            //Request that the client validate the inputted smartcard
            out.println("Valid:" + ID);
            //Wait for an answer from the server
            while ((answer = scan.nextLine()) == null) {
                sleep(1);
            }
        } catch (Exception e) {//Throw a no connection error to cause the barrier to try and re-connect to the client
            throw new NoConnectionError("No connection to the server.");
        }
        return Boolean.parseBoolean(answer);//Return if the smartcard is valid or not
    }//Gets the validity of the inputted smartcard ID

    /**
     * Method for requesting an update of the carpark details.
     *
     * @throws NoConnectionError Thrown to initiate reconnect to cilent
     * @return A String Json of a 4 element object array
     * @since 1.0.0
     */
    public String update(Scanner scan, PrintWriter out) throws NoConnectionError {
        String answer;
        try {
            out.println("Update");//Request an update
            while ((answer = scan.nextLine()) == null) {//Wait for an answer from the client
                sleep(5);
            }
        } catch (Exception e) {//Throw a no connection error to cause the barrier to try and re-connect to the client
            throw new NoConnectionError("No connection to the server.");
        }
        return answer;
    }//Gets a Json with the carpark details

    /**
     * Method to wait a random amount of milliseconds up to the inputted amount.
     * @param time The maximum number of milliseconds the thread will sleep for.
     *
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
