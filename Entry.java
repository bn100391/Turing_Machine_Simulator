import java.util.*; 

public class Entry {
    private State nextState; 
    private String tapeWrite;
    private String direction; 
    boolean toError;
    
    //Only use on valid transitions
    public Entry(){
        this.nextState = null; 
        this.tapeWrite = null; 
        this.direction = null; 
        this.toError = false; 
    }

    //Construction for transitions to error states 
    public Entry(boolean toError){
        this.toError = toError; 
    }

    //Constructors for valid transitions.
    public Entry(State nextState, String tapeWrite, String direction){
        this.nextState = nextState; 
        this.tapeWrite = tapeWrite; 
        this.direction = direction; 
        this.toError = false; 
    }

    public State getNextState() {
        return nextState;
    }

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    public String getTapeWrite() {
        return tapeWrite;
    }

    public void setTapeWrite(String tapeWrite) {
        this.tapeWrite = tapeWrite;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isToError() {
        return toError;
    }

    public void setToError(boolean toError) {
        this.toError = toError;
    }

}