import java.io.PrintWriter;
import java.util.Scanner;

public class Protocol {

    public boolean getType(Scanner scan){
        String line;
        //Wait for an input
        while((line = scan.nextLine()) == null){
            sleep(1);
        }
        return Boolean.parseBoolean(line);
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

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }//Puts the thread to sleep for the inputted amount of time
}
