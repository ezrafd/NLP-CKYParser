package nlp.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CKYParser {
    protected ArrayList<ArrayList<ArrayList<HashMap<String, EntryInfo>>>> tableList;
    protected HashMap<String, ArrayList<GrammarRule>> lexicalMap;
    protected HashMap<String, ArrayList<GrammarRule>> unaryMap;
    protected ArrayList<GrammarRule> binaryMap;


    public CKYParser (String rulesFile, String inputText){

        //initiates table and hashmaps
        tableList = new ArrayList<ArrayList<ArrayList<HashMap<String, EntryInfo>>>>();
        lexicalMap = new HashMap<String, ArrayList<GrammarRule>>();
        unaryMap = new HashMap<String, ArrayList<GrammarRule>>();
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
                    lexicalMap.get(gr.getRhs().get(0)).add(gr);
                } else if (gr.numRhsElements() == 1) {
                    if (!unaryMap.containsKey(gr.getRhs().get(0))) {
                        unaryMap.put(gr.getRhs().get(0), new ArrayList<>());
                    }
                    unaryMap.get(gr.getRhs().get(0)).add(gr);
                } else if (gr.numRhsElements() == 2) {
                    binaryMap.add(gr);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        parseFile(inputText);
    }

    public void parseFile(String inputText) {
        /* Start writing your parsing method. Create a new CKY table (two dimensions) and fill in the
            diagonals based on the lexical components. I would suggest writing a method that adds
            a constituent to your table (either in your entry class or as a standalone method). Print out
            the added constituents (either as they’re added or by printing out the table) and make sure
            everything is added appropriately. You can compare against your hand-written example(s).
             */
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputText));
            int tableIndex = 0;
            ArrayList<ArrayList<HashMap<String, EntryInfo>>> table;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\\s+");
                table = new ArrayList<>(splitLine.length);
                populateTable(splitLine, table);
                addFirstDiagonal(splitLine, table);
                addUpperTriangle(splitLine, table);

                tableList.add(table);
                System.out.println(table);
            }

            //close the reader
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateTable(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {
        for (int i = 0; i < splitLine.length; i++) {
            table.add(new ArrayList<>(splitLine.length));
            for (int x = 0; x < i; x++) {
                table.get(i).add(x, null);
            }
            for (int j = i; j < splitLine.length; j++) {
                table.get(i).add(j, new HashMap<>());
            }
        }

    }

    public void addFirstDiagonal(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {
        int k = 0;
        while (k < splitLine.length) {
            System.out.println(splitLine[k]);
            ArrayList<GrammarRule> lexList = lexicalMap.get(splitLine[k]);
            System.out.println(lexList);
            // Needs fixing, shouldn't be words that aren't in the grammar?
            if (lexList != null) {
                for (GrammarRule gr : lexList) {

                    Reference wordRef = new Reference(gr.getRhs().get(0));
                    EntryInfo ei = new EntryInfo(gr.getWeight(), wordRef);

                    if (unaryMap.containsKey(gr.getLhs())) {
                        Reference unaryRuleRef = new Reference(wordRef.getCons(), wordRef.getI(), wordRef.getJ());
                        double newWeight;
                        for (GrammarRule rule : unaryMap.get(gr.getLhs())) {
                            newWeight = rule.getWeight() + ei.getWeight();
                            EntryInfo unaryInfo = new EntryInfo(newWeight, unaryRuleRef);
                            table.get(k).get(k).put(rule.getLhs(), unaryInfo);
                        }
                    }

                    // Put the constituent in the cell at (k, k)
                    table.get(k).get(k).put(gr.getLhs(), ei);
                }
            }
            k++;
        }
    }

    public void addUpperTriangle(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {
        // fill in the upper triangle of the table
        for (int j = 1; j < splitLine.length; j++) {
            System.out.println("j"+j);
            for (int i = j-1; i >= 0; i--) {
                System.out.println("i"+i);
                for (int k = i+1; k <= j; k++) {
                    System.out.println("k"+k);

                    for (GrammarRule rule : binaryMap) {
                        String rhs1 = rule.getRhs().get(0);
                        String rhs2 = rule.getRhs().get(1);
                        if (table.get(i).get(k-1).containsKey(rhs1) && table.get(k).get(j).containsKey(rhs2)) {
                            System.out.println("True");
                            Reference ref1 = new Reference(rhs1, i, k);
                            Reference ref2 = new Reference(rhs2, k, j);
                            double newWeight = rule.getWeight()
                                    + table.get(i).get(k-1).get(rhs1).getWeight()
                                    + table.get(k).get(j).get(rhs2).getWeight();
                            EntryInfo ei = new EntryInfo(newWeight, ref1, ref2);

                            // Put the constituent in the cell at (i, j)
                            table.get(i).get(j).put(rule.getLhs(), ei);
                        }
                    }
                }
            }
        }
    }

    public void addConstituent(){};

    public static void main (String args[]) {
        String rulesFile = "/Users/ezraford/Desktop/School/CS 159/NLP-CKYParser/assign4-starter/data/example.pcfg";
        String inputFile = "/Users/ezraford/Desktop/School/CS 159/NLP-CKYParser/assign4-starter/data/example.input";
        CKYParser ckyParser = new CKYParser(rulesFile, inputFile);
    }
}
