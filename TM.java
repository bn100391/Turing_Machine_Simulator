import java.util.*; 

public class TM{
    private Set<String> alphabet; 
    private Set<String> tape_alphabet; 
    private Set<State> states; 
    private Map<State, Map<String, Entry>> transitionTable;

    public TM(){
        alphabet = new LinkedHashSet<>(); 
        tape_alphabet = new LinkedHashSet<>(); 
        states = new LinkedHashSet<>(); 
        transitionTable = new LinkedHashMap<>(); 
    }

    public State getStartState() {
        for(State s: transitionTable.keySet()){
            if(s.isStart()){
                return s; 
            }
        }
        System.err.printf("ERROR: No Start State Found\n"); 
        return null; 
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public State getAcceptState() {
        for(State s: transitionTable.keySet()){
            if(s.isAccept()){
                return s; 
            }
        }
        return null; 
    }

    public State getRejectState() {
        for(State s: transitionTable.keySet()){
            if(s.isReject()){
                return s;   
            }
        }
        return null; 
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = alphabet;
    }

    public Set<String> getTape_alphabet() {
        return tape_alphabet;
    }

    public void setTape_alphabet(Set<String> tape_alphabet) {
        this.tape_alphabet = tape_alphabet;
    }

    public Map<State, Map<String, Entry>> getTransitionTable() {
        return transitionTable;
    }

    public void setTransitionTable(Map<State, Map<String, Entry>> transitionTable) {
        this.transitionTable = transitionTable;
    }

    public boolean isStateName(String input){
        for(State s: states){
            if(s.getLabel().equals(input)){
                return true; 
            }
        }
        return false; 
    }

    public State getStateWithName(String name){
        for(State s: states){
            if(s.getLabel().equals(name)){
                return s; 
            }
        }
        System.err.printf("No state with that name %s\n", name); 
        return null; 
    }

    public void putTransition(State startState, String symbolRead, Entry e){
        Map<String, Entry> read_to_entry = new LinkedHashMap<>(); 
        read_to_entry.put(symbolRead, e); 
        this.transitionTable.put(startState, read_to_entry); 
    }

    public Entry getEntry(State startState, String symbol){
        return transitionTable.get(startState).get(symbol); 
    }

}