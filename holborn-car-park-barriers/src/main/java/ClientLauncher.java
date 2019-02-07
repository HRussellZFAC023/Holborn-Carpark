public class ClientLauncher {

    public static void main(String[] args) {
        new ClientLauncher();
    }

    public ClientLauncher() {
        Thr thread = new Thr(
                false,
                new Ticket("Ticket")
        );//Create a new test thread
        thread.start();
    }//Class used for testing the client connections

    private class Thr extends Thread {

        private boolean type;//Type of barrier
        private Ticket ticket;//Ticket if applicable

        public Thr(boolean type, Ticket ticket){
            new Thr(type);//Call the alternate constructor
            this.ticket = ticket;//Pass in teh ticket
        }//Constructor for test out barrier

        public Thr(boolean type){
            this.type = type;//Set the type of barrier being simulated
        }//Constructor for test in barrier

        @Override
        public void run() {
            if (type) {
                new Client(type);
            } else {
                new Client(type, ticket);
            }
        }//Run thread
    }//Thread for testing socket connections of multiple barriers
}
