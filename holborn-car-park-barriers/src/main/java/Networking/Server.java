package Networking;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private int port = 4444;//Port that the server listens on
    private MultiServerThread server;//holds the most recent server thread
    private boolean listening = true;

    public static void main(String[] args) {
        new Server();
    }

    public void stopListening(){
        listening = false;
    }

    public Server() {
        try (ServerSocket serverSocketIn = new ServerSocket(port)){
            //Whilst listening for connections
            while (listening) {
                //Wait for a device to try and connect on the listening port, then create a thread for it
                server = new MultiServerThread(serverSocketIn.accept());
                //Start the server thread.
                server.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }
}
