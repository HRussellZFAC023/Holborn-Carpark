package uk.co.holborn.carparkbarriers;

/**
 * Class used to store teh value of a position on the QR code
 *
 * @author Cameron
 * @version 1.0.0
 */
public class Pix {

    private boolean state;

    /**
     * Constructor that sets the value of the position
     *
     * @param state The value of the position
     * @since 1.0.0
     */
    public Pix(boolean state){
        this.state = state;
    }

    /**
     * Getter for the value of the pixel
     *
     * @return The value of the position
     * @since 1.0.0
     */
    public boolean getState(){
        return state;
    }

    /**
     * Flips teh value of the pixel
     *
     * @since 1.0.0
     */
    public void mask(){
        state = !state;
    }
}
