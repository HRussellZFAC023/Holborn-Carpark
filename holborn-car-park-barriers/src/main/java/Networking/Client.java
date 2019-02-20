package Networking;

import FxStuff.Controllers.LandingPageController;
import FxStuff.Controllers.MainViewController;
import FxStuff.InfoPopUp;
import FxStuff.Scenes;
import FxStuff.Ticket;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Thread {

    //private String ip = "127.0.0.1";//Needs to be changed
    //private String port = "4444";//The port that the barriers connect to
    private Socket socket;

    private MainViewController mainCont;

    public Client(MainViewController mainCont) {
        this.mainCont = mainCont;

    }//Constructor for barrier

    /*public Client(boolean type, Ticket ticket) {
        connect(type);//Connect to server
        System.out.println("Ticket is: " + validateTicket(ticket));//Check if ticket is valid
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
        endConnection();//End connection to server
        System.out.println("Ended " + (type ? "Input" : "Output") + " thread.");
    }//Constructor for test out barrier*/

    @Override
    public void run() {
        connect();
    }

    public void connect() {
        //Set the variables for the socket definition in the try, catch
        InfoPopUp popup =  mainCont.getPopup();
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
        //logger.info("Connected to the web server. Authorising...");
        popup.show("Connected! Authorising...");
        mainCont.disconnectedUI(true);
        sleep(500);//Add an authorisation method
        popup.show("Authorised", false);
        popup.removePopUp();
        //logger.info("Authorised!");
        LandingPageController lc = (LandingPageController) Scenes.LANDING.getController();
        Platform.runLater(lc::enableFetching);
        mainCont.disconnectedUI(false);

        System.out.println("Connection made on: " + hostname + ":" + portNumber + ".");
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
    }

    /*private void socketPreparation() {
        socket.on(io.socket.client.Socket.EVENT_CONNECT, args_cn -> {
            logger.info("Connected to the web server. Authorising...");
            popup.show("Connected! Authorising...");
            disconnectedUI(true);
            socket.emit("authorisation", GlobalVariables.CAR_PARK_ID, (Ack) objects -> {
                if (objects[0].equals(200)) {
                    popup.show("Authorised", false);
                    popup.removePopUp();
                    logger.info("Authorised!");
                    LandingPageController lc = (LandingPageController) Scenes.LANDING.getController();
                    Platform.runLater(lc::enableFetching);
                    disconnectedUI(false);
                } else {
                    logger.error("Unauthorised access! Please check that the information from the config file are correct or check the database connection.");
                    System.exit(0);
                }
            });
        });
        socket.on(io.socket.client.Socket.EVENT_CONNECTING, args_cni -> {
            popup.show("Connecting...");
            disconnectedUI(true);
            // logger.info("Connecting...");
        });
        socket.on(io.socket.client.Socket.EVENT_RECONNECTING, args_cni -> {
            popup.show("Reconnecting...");
            disconnectedUI(true);
        });
        socket.on(io.socket.client.Socket.EVENT_CONNECT_ERROR, args_cni -> System.out.println("Err" + args_cni[0]));
        socket.on(io.socket.client.Socket.EVENT_DISCONNECT, args_dc -> {
            logger.warn("Disconnected");
            disconnectedUI(true);
            sceneManager.changeTo(Scenes.LANDING);
            popup.show("Disconnected");
        });
        socket.connect();
    }*/
}
