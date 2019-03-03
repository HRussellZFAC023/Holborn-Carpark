package QRCode;

public class Pix {

    private boolean state;

    public Pix(boolean state){
        this.state = state;
    }

    public boolean getState(){
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void mask(){
        state = !state;
    }
}
