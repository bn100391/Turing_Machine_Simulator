import java.util.*; 

public class State implements Comparable<State>{
    private String label; 
    private boolean isAccept; 
    private boolean isReject; 
    private boolean isStart;

    /**
     * @param label
     * @param isAccept
     * @param isStart 
     */
    public State(){
        this.label = ""; 
        this.isAccept = false; 
        this.isReject = false; 
        this.isStart = false; 
    }

    /**
     * @param label
     * @param isAccept
     * @param isReject 
     * @param isStart 
     */
    public State(String label, boolean isAccept, boolean isReject, boolean isStart){
        this.label = label; 
        this.isAccept = isAccept;
        this.isReject = isReject;  
        this.isStart = isStart; 
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean isAccept) {
        this.isAccept = isAccept;
    }

    public boolean isReject() {
        return isReject;
    }

    public void setReject(boolean isReject) {
        this.isReject = isReject;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public boolean equals(State that){
        if(this.getLabel().equals(that.getLabel())){
            return true; 
        }
        return false; 
    }

    @Override
    public int compareTo(State that) {
       return this.getLabel().compareTo(that.getLabel()); 
    }

}