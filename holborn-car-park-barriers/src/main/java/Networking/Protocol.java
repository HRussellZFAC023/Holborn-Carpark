package Networking;

import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol {

    public boolean getType(Scanner scan){
        String line;
        //Wait for an input
        while((line = scan.nextLine()) == null){
            sleep(1);
        }
        return line.equalsIgnoreCase("In");
    }//Protocol for getting the type of barrier

    public String requestTicket(Scanner scan, PrintWriter out) {
        String request;
        out.println("Get");//Send the get command to the server to request a ticket
        //Wait for the server to send a ticket
        while ((request = scan.nextLine()) == null) {
            sleep(1);
        }
        return request;
    }//Requests a ticket from the server

    public boolean validate(String ID, Scanner scan, PrintWriter out){
        out.println("Check:"+ID);
        String answer;
        //Wait for an answer from the server
        while ((answer = scan.nextLine()) == null) {
            sleep(1);
        }
        return Boolean.parseBoolean(answer);
    }//Checks if the input ID from the barrier is a valid ticket to leave the carpark

    public String update(Scanner scan, PrintWriter out){
        System.out.println("Sending update request");
        out.println("Update");
        System.out.println("Sent request");
        String answer;
        while ((answer = scan.nextLine()) == null) {
            System.out.println("Null answer.");
            sleep(1);
        }
        System.out.println("Update recieved from socket.");
        return answer;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }//Puts the thread to sleep for the inputted amount of time
}
