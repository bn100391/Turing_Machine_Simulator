import java.util.*;
import java.io.*; 


public class Driver{
    static String e_label = "epsilon"; 

    static TM load_TM_States(String filepath, TM tm){
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line = reader.readLine(); 
            boolean skippedAlphabetRow = false; 

            while(line != null){
                Scanner parser = new Scanner(line); 
                parser.useDelimiter("[ \t\n]+");
                String token = ""; 

                if(parser.hasNext()){
                    token = parser.next(); 
                }

                if(token.equals("#") || line.isEmpty()){ //Ignore comment lines 
                    line = reader.readLine(); 
                    continue;   
                } 

                if(!skippedAlphabetRow){
                    skippedAlphabetRow = true; 
                    line = reader.readLine(); 
                    continue; 
                }
                         
                State state = null; 
                boolean is_start = false; 
                boolean is_accept = false; 
                boolean is_reject = false; 
                boolean state_processed = false; 
                
                    while(!state_processed && skippedAlphabetRow){ //Process Curr State 
                        if(token.equals("->")){ //Checks for marker 
                            is_start = true; 
                        }
                        else if(token.equals("*")){ //Checks for marker
                            is_accept = true; 
                        }else if(token.equals("!")){
                            is_reject = true; 
                        }else{ //Process start_state itself. 
                            Set<State> all_states = tm.getStates(); 
                            int state_found = -1; 
                            for(State s: all_states){ //Check to see if your reading in a duplicate state 
                                if(s.getLabel().equals(token)){
                                    state_found = 0; 
                                }
                            }
                            if(state_found == 0){ 
                                System.err.println("Duplicate states exist in the input\n"); 
                            }else{
                                state = new State(token, is_accept, is_reject, is_start); 
                                state_processed = true; 
                                tm.getStates().add(state); 
                            }
                        }
                        if(parser.hasNext()){
                            token = parser.next(); 
                        }else{
                            break; 
                        } 
                    }
                    line = reader.readLine(); 
                }

        } catch(Exception e) {
            e.printStackTrace(); 
        }

        return tm; 
    }


    static TM load_TM_Alphabet(String filepath, TM tm){
        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line = reader.readLine(); 
            boolean proccessedAlphabet = false; 
            int k = -1; 
            int numRead = 0; 

            while(line != null && !proccessedAlphabet){
                Scanner parser = new Scanner(line); 
                parser.useDelimiter("[ \t\n]+");
                String token = ""; 

                if(parser.hasNext()){
                    token = parser.next(); 
                }

                if(token.equals("#") || line.isEmpty()){ //Ignore comment lines 
                    line = reader.readLine(); 
                    continue;   
                } 
                
                for(;;){ 
                    if(token.equals("|")){
                        proccessedAlphabet = true; 
                        if(parser.hasNext()){
                            token = parser.next(); 
                        }else{
                            break; 
                        } 
                        continue; 
                    }
                    else if(numRead == 0){
                        k = Integer.parseInt(token); 
                        numRead++; 
                    }else{
                        tm.getTape_alphabet().add(token);
                        if(numRead <= k){
                            tm.getAlphabet().add(token); 
                        } 
                        numRead++; 
                    }

                    if(parser.hasNext()){
                        token = parser.next(); 
                    }else{
                        break; 
                    } 
                }

                line = reader.readLine(); 
            }

        } catch(Exception e) {
            e.printStackTrace(); 
        }

        return tm; 
    }


    static TM loadTM(String filepath){
  
        TM blankTM = new TM(); 
        TM tmWithStates = load_TM_States(filepath, blankTM);
        TM tm = load_TM_Alphabet(filepath, tmWithStates); 

        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line = reader.readLine(); 
            boolean skippedAlphabetRow = false; 

            while(line != null){
                Scanner parser = new Scanner(line); 
                parser.useDelimiter("[ \t\n]+");
                String token = ""; 
                State startState = null; 
                boolean noMoreTokens = false; 
                Set<String> hasNoTransition = new LinkedHashSet<>(); 
                for(String s: tm.getTape_alphabet()){
                    hasNoTransition.add(s); 
                }

                if(parser.hasNext()){
                    token = parser.next(); 
                }

                if(token.equals("#") || line.isEmpty()){ //Ignore comment lines 
                    line = reader.readLine(); 
                    continue;   
                } 

                if(!skippedAlphabetRow){
                    skippedAlphabetRow = true; 
                    line = reader.readLine(); 
                    continue; 
                }

                while(!token.equals("|")){ //Read until bar
                    if(tm.isStateName(token)){
                        startState = tm.getStateWithName(token); 
                    }
                    if(parser.hasNext()){
                        token = parser.next(); 
                    }
                }
                if(!parser.hasNext()){
                    noMoreTokens = true; 
                }else{
                    token = parser.next(); //Move after bar
                }


                       
                if(startState == null){
                    System.err.println("NULL startState");
                }
                boolean isAjdForm = false; 
                
                
                Map<String, Entry> entryQueue = new HashMap<>();
                Map<String, Entry> symbolNext = new HashMap<>();  
                for(String symbolRead: tm.getTape_alphabet()){ 
                    
                    if(entryQueue.containsKey(symbolRead)){
                        Entry e = entryQueue.get(symbolRead); 
                        symbolNext.put(symbolRead, e);
                        entryQueue.remove(symbolRead); 
                        if(parser.hasNext()){
                            token = parser.next(); 
                        }else{
                            noMoreTokens = true; 
                        } 
                        continue; 
                    }

                    if(noMoreTokens){
                        Entry e = new Entry(true);
                        if(hasNoTransition.contains(symbolRead)){
                            symbolNext.put(symbolRead, e);
                        } 
                        
                        continue; 
                    }

                    if(token.charAt(0) != '(' && token.charAt(0) != '-' && !isAjdForm && !noMoreTokens){
                        isAjdForm = true; 
                    }
                   
                    if(isAjdForm){
                        String[] symbolsThenTransition = token.split("[=]+");
                        token = symbolsThenTransition[1];
                        for(int i = 0; i < symbolsThenTransition[0].length(); i++){
                            String currSymbolRead = Character.toString(symbolsThenTransition[0].charAt(i)); 
                            Entry e = parseEntry(tm, token, isAjdForm, startState, currSymbolRead);
                            symbolNext.put(currSymbolRead, e); 
                            hasNoTransition.remove(currSymbolRead); 
                        } 

                    }else{
                        Entry e = parseEntry(tm, token, isAjdForm, startState, symbolRead); 
                        symbolNext.put(symbolRead, e);  
                    }

                    if(parser.hasNext()){
                        token = parser.next(); 
                    }else{
                        noMoreTokens = true; 
                    } 

                }
            if(isAjdForm && !hasNoTransition.isEmpty()){
                for(String s: hasNoTransition){
                    Entry e = new Entry(true); 
                    symbolNext.put(s, e);
                }
            }
            tm.getTransitionTable().put(startState, symbolNext); 
            line = reader.readLine(); 
            }

        } catch(Exception e) {
            e.printStackTrace(); 
        }

        return tm; 
    }
 

    static Entry parseEntry(TM tm, String token, boolean isAjdForm, State startState, String symbolRead){
        Entry e = null; 

        if(token.equals("-") && !isAjdForm){ //Error transition, return error 
            e = new Entry(true); 
            return e; 
        }
        e = new Entry(); 
        
        token = token.substring(1, token.length()-1); 
        String[] transitionBody = token.split("[(),]", -1); 
        State nextState; 
        String tapeWrite; 
        String direction = transitionBody[2]; 
            
        if(transitionBody[0].equals("")){
            nextState = startState; 
        }else{
            nextState = tm.getStateWithName(transitionBody[0]); 
        }

        if(transitionBody[1].equals("")){
            tapeWrite = symbolRead; 
        }else{
            tapeWrite = transitionBody[1]; 
        }

        e.setNextState(nextState); 
        e.setTapeWrite(tapeWrite); 
        e.setDirection(direction); 

        return e; 
    }


    static void print_t_table(TM tm){
        Map<State, Map<String, Entry>> tt = tm.getTransitionTable(); 

        System.out.printf("\n%15s", "|"); 
        for(String s: tm.getTape_alphabet()){ //Print out input symbols 
            if(s.equals("epsilon")){
                System.out.printf("%36s ", s);
            }else{
                System.out.printf("%15s ", s); 
            }
        }

        for(State curr_state: tt.keySet()){ //Print out body of transition table 
            System.out.printf("\n");
            Map<String, Entry> symbol_next = tm.getTransitionTable().get(curr_state);
            
            String marker = ""; 
            if(curr_state.isStart()){
                marker = marker + "-> "; 
            }
            if(curr_state.isAccept()){
                marker = marker + "* "; 
            }
            if(curr_state.isReject()){
                marker = marker + "!"; 
            }
            if(!curr_state.getLabel().equals("SYMBOL_ROW")){
                System.out.printf("%-5s %-7s %s ", marker, curr_state.getLabel(), "|"); 
            }
            System.out.printf("%4s", ""); 
            
            for(String input_symbol: tm.getTape_alphabet()){ //Print out next state configuartions. 
                Entry nextConfig = symbol_next.get(input_symbol); 
                if(nextConfig.isToError()){
                    System.out.printf("%8s%8s", "-","");  
                }else{
                    System.out.printf("%6s(%s,%s,%s) ", "", nextConfig.getNextState().getLabel(), nextConfig.getTapeWrite(), nextConfig.getDirection()); 
                }
            }
            System.out.printf("\n");
        }
        
    }


    static void print_info(TM tm){
            
        System.out.printf("States: {"); 
        int a = 0; 
        for(State s: tm.getStates()){
            if(a < 1){
                System.out.print(s.getLabel()); 
            }else{
                System.out.printf(", %s", s.getLabel()); 
            }
            a++; 
        }
        System.out.printf("}\n"); 

        System.out.printf("Input alphabet: {"); 
        int b = 0; 
        for(String inputSymbol: tm.getAlphabet()){
            if(inputSymbol.equals(e_label)){
                continue; 
            }
            if(b < 1){
                System.out.print(inputSymbol); 
            }else{
                System.out.printf(", %s", inputSymbol); 
            }
            b++; 
        }
        System.out.printf("}\n"); 

        System.out.printf("Tape alphabet: {"); 
        int c = 0; 
        for(String inputSymbol: tm.getTape_alphabet()){
            if(inputSymbol.equals(e_label)){
                continue; 
            }
            if(c < 1){
                System.out.print(inputSymbol); 
            }else{
                System.out.printf(", %s", inputSymbol); 
            }
            c++; 
        }
        System.out.printf("}\n"); 

        System.out.println("Transition Table:"); 
        print_t_table(tm); 

        System.out.printf("Initial State: %s\n", tm.getStartState().getLabel()); 
        System.out.printf("Accept State: %s\n", tm.getAcceptState().getLabel()); 
        System.out.printf("Reject State: %s\n", tm.getRejectState().getLabel()); 
    }


    static void lang(TM tm, int lengthLimit, int moveLimit){
 
        TreeSet<String> acceptedStrings = new TreeSet<String>(Comparator.comparingInt(String::length).thenComparing(String::toString)); 
        TreeSet<String> limitStrings = new TreeSet<String>(Comparator.comparingInt(String::length).thenComparing(String::toString)); 
        Set<String> alphabet = tm.getAlphabet(); 
        boolean e = alphabet.remove(e_label);
        if(e){
            acceptedStrings.add("\"\""); 
        } 
        Set<String> possibleStrings = gen_strings(alphabet, lengthLimit); 
 
   
        if(runTM(tm, "", moveLimit, false) == "ACCEPT"){
            acceptedStrings.add("\"\""); 
        }

        //Filter out strings not in language 
        for(String s: possibleStrings){
            String result = runTM(tm, s, moveLimit, false); 
            if(result == "REJECT"){
                continue; 
            }else if(result == "ACCEPT"){
                acceptedStrings.add(s); 
            }else if(result.equals("LIMIT")){
                limitStrings.add(s);  
            }
            else{
                System.err.println("Error producing strings of language"); 
                System.exit(1); 
            }
        }

        System.out.printf("L(M) = {"); 
        int i = 0; 
        for(String s: acceptedStrings){
            if(i < 1){
                if(s.equals("")){
                    System.out.printf("\n  \"\"");  
                }else{
                    System.out.printf("\n  %s", s); 
                }
                
            }else{
                if(s.equals("")){
                    System.out.printf(",\n\"\""); 
                }else{
                    System.out.printf(",\n  %s", s); 
                }
                
            }
            i++; 
        }
        System.out.printf(",\n}\n"); 

        if(!limitStrings.isEmpty()){
            System.out.printf("Undetermined strings (due to limit):\n"); 
            for(String l: limitStrings){
                System.out.printf("  %s\n", l); 
            }
        }

    }


    static String runTM(TM tm, String input, int moveLimit, boolean print){
        if(print){
            System.out.printf("Running on input [%s] with limit %d:\n", input, moveLimit); 
        }
    

        State currState = tm.getStartState(); 
        List<String> tape = new LinkedList<>(); 
        for(int i = 0; i < input.length(); i++){ //Put input string on tape. 
            char currSymbol = input.charAt(i); 
            tape.add(i, Character.toString(currSymbol)); 
        }

        String currSymbol = "JUNK_SYMBOL"; 
        if(tape.isEmpty()){
            currSymbol = "_"; 
            tape.add(0, "_"); 
        }else{
            currSymbol = tape.get(0); 
        }

        
        String direction = ""; 

        int tapeIndex = 0; 
        for(int move = 0; move <= moveLimit; move++){
            int printI = 0; 
            if(print){
                System.out.printf("Config: "); 
            }
           
            for(String s: tape){ //Prints current configuration. 
                if(printI == tapeIndex){
                    if(print){
                        System.out.printf("<%s>", currState.getLabel()); 
                    }
                }
                if(print){
                    System.out.printf("%s", s); 
                }
                printI++; 
            } 
            if(print){
                System.out.println(); 
            }
          
             
            if(currState.isAccept()){
                return "ACCEPT"; 
            }
            if(currState.isReject()){
                return "REJECT"; 
            }

            Entry e = tm.getEntry(currState, currSymbol);
            if(e.isToError()){
                tape.add("_"); 
                tapeIndex++; 
                currState = tm.getRejectState(); 
                continue; 
            }
                currState = e.getNextState(); 
                tape.set(tapeIndex, e.getTapeWrite()); 
    
                direction = e.getDirection(); 
                if(direction.equals("L")){
                    tapeIndex--; 
                }else if(direction.equals("R")){
                    if(tapeIndex == tape.size()-1){
                        tape.add("_"); 
                    }
                    tapeIndex++; 
                }
 
            currSymbol = tape.get(tapeIndex); 
        }

        return "LIMIT"; 
    }


    static Set<String> gen_strings(Set<String> alphabet, int lengthLimit){
        Set<String> possibleStrings = new TreeSet<String>(Comparator.comparingInt(String::length).thenComparing(String::toString)); 
        Set<String> buffSet = new TreeSet<String>(Comparator.comparingInt(String::length).thenComparing(String::toString));
        //Produce every possible string in langauge of that length; 
        for(int strIndex = 0; strIndex < lengthLimit; strIndex++){
            for(String symbol: alphabet){
                if(possibleStrings.size() < alphabet.size()){
                    possibleStrings.add(symbol); 
                }else{
                    for(String s: possibleStrings){          
                        buffSet.add(s + symbol);             
                    }

            }
        }
        for(String s: buffSet){
            possibleStrings.add(s); 
        }
        buffSet.clear(); 
        }

        return possibleStrings; 
    }


    public static void main(String[] args){

        if(args.length < 2){
            System.err.println("Please enter valid arguments"); 
            System.exit(1); 
        }

        if(args[0].equals("--info")){
            TM tm = loadTM(args[1]); 
            print_info(tm); 
        }
        else if(args[0].equals("--run")){
            String path = args[1]; 
            String input = args[2]; 
            int moveLimit = Integer.parseInt(args[3]); 
            TM tm = loadTM(path); 
            System.out.printf("%s\n", runTM(tm, input, moveLimit, true)); 
        }
        else if(args[0].equals("--language")){
            String path = args[1]; 
            int lengthLimit = Integer.parseInt(args[2]); 
            int moveLimit = Integer.parseInt(args[3]); 
            TM tm = loadTM(path); 
            lang(tm, lengthLimit, moveLimit); 
        }else{
            System.err.println("Please enter valid arguments\n"); 
            System.exit(1); 
        }
    
    }
}
