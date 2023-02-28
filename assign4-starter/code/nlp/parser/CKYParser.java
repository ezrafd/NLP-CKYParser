package nlp.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CKYParser {
    protected ArrayList<ArrayList<HashMap<String, EntryInfo>>> table;
    protected HashMap<String, ArrayList<String>> lexicalMap;
    protected HashMap<String, ArrayList<String>> unaryMap;
    protected ArrayList<GrammarRule> binaryMap;




    public CKYParser (String rulesFile, String inputText){

        //initiates table and hashmaps
        table = new ArrayList<ArrayList<HashMap<String, EntryInfo>>>();
        lexicalMap = new HashMap<String, ArrayList<String>>();
        unaryMap = new HashMap<String, ArrayList<String>>();
        binaryMap = new ArrayList<GrammarRule>();

        //creates buffered reader and reads each rule
        try {
            BufferedReader reader = new BufferedReader(new FileReader(rulesFile));
            String line;
            while ((line = reader.readLine()) != null) {
                GrammarRule gr = new GrammarRule(line);

                //checks if rule is lexical, unary, or binary and adds it to the relevant hashmap
                if (gr.isLexical()){
                    if (!lexicalMap.containsKey(gr.getRhs().get(0))) {
                        lexicalMap.put(gr.getRhs().get(0), new ArrayList<>());
                    }
                    lexicalMap.get(gr.getRhs().get(0)).add(gr.getLhs());
                } else if (gr.numRhsElements() == 1) {
                    if (!unaryMap.containsKey(gr.getRhs().get(0))) {
                        unaryMap.put(gr.getRhs().get(0), new ArrayList<>());
                    }
                    unaryMap.get(gr.getRhs().get(0)).add(gr.getLhs());
                } else if (gr.numRhsElements() == 2) {
                    binaryMap.add(gr);
                }
            }
            
            reader = new BufferedReader(new FileReader(inputText));
            int tableIndex = 0;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\\s+");
                int k = 0;
                while (k < splitLine.length - 1){
                    splitLine[k]
                }
            }

            //close the reader
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
