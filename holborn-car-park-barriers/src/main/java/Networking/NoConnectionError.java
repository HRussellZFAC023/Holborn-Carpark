package Networking;

public class NoConnectionError extends Exception {
    public NoConnectionError(String errorMessage) {
        super(errorMessage);
    }
}
