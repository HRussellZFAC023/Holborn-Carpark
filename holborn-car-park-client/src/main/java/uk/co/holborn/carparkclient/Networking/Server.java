/*
 * Copyright (c) 2019. Ionut-Vlad Alboiu.
 */

package uk.co.holborn.carparkclient.Networking;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.holborn.carparkclient.Alerter;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server is a class that listens for barriers connecting and allows mass communication with currently connected ones.
 *
 * @author Cameron
 * @version 1.0.0
 */
public class Server extends Thread {

    //TODO Change sout's to work with logger
    //TODO Change error messages to work with logger

    private int port;//Port that the server listens on
    private boolean listening = true;//Boolean that defines whether the server is running
    private List<MultiServerThread> connections = new ArrayList<>();//List of all the currently connected barriers
    private final Logger logger;

    /**
     * The class constructor, setting the port that the server listens on.
     *
     * @param port The port to listen for connections on.
     * @since 1.0.0
     */
    public Server(int port) {
        super("Server thread");//Set the name of the thread
        this.port = port;//Set the port
        this.setDaemon(true);//Set as a daemon so that if all other main threads are stopped this one stops as well
        logger = LogManager.getLogger(getClass().getName());
    }//Constructor, set the port to listen on

    /**
     * Method for stopping the server thread and terminating all connections between barriers.
     *
     * @since 1.0.0
     */
    public void shutdown() {
        stopListening();//Stop the server listening for more connections
        if (connections.size() > 0) {
            for (int pos = connections.size() - 1; pos >= 0; pos--) {//Loop through every connection
                connections.get(pos).shutdown();//Set it to terminate the connection.
                connections.remove(pos);//Remove the server from the list.
            }
        }
    }//Stops the server listening for more connections then closes all current connenctions

    /**
     * Method for updating the information on all the connected barriers.
     *
     * @since 1.0.0
     */
    public void update() {
        List<MultiServerThread> conRemove = new ArrayList<>();
        for (MultiServerThread con : connections) {//Loop through all the connected barriers
            try{
            con.update();//Set the barrier to request the carpark details off of the server
        }catch(NoConnectionError ignore){
                conRemove.add(con);
            }}
        for (MultiServerThread con: conRemove){
            connections.remove(con);
        }
    }//Makes all the connected barriers update the carpark info

    /**
     * Method for stopping the client listening for more barriers.
     *
     * @since 1.0.0
     */
    private void stopListening() {
        listening = false;//Set listening to false to stop the thread connecting to more barriers
        //Socket terminateSocket = ;
    }//Stops the thread accepting connections

    /**
     * Method that listens on the specified port for more barriers and forms new
     * connections with them on other unused ports.
     *
     * @since 1.0.0
     */
    @Override
    public void run() {
        try (ServerSocket serverSocketIn = new ServerSocket(port)) {
            logger.info("Started server on port: " + port);
            int barrier = 0;
            while (listening) {
                //Wait for a device to try and connect on the listening port, then given a new port to use and a thread to run it
                MultiServerThread newServer = new MultiServerThread(serverSocketIn.accept());
                //Add the new connection to the list of connections
                connections.add(newServer);
                newServer.setName("Barrier: " + barrier++);
                //Start the newServer thread.
                newServer.start();
            }
        } catch (IOException e) {
           logger.error("Could not start server on port " + port);
            Platform.runLater((() ->  Alerter.showUnableToStartAlert("Unable to start the barrier server", e.getMessage())));

        }
    }//Listens on the inputted port for connections
}
