package nlp.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tal + Ezra
 * a parser that is initialized based on a binary grammar. Once it is initialized, it is able to
 * parse multiple sentences without having to reread the grammar.
 */
public class CKYParser {
    protected ArrayList<ArrayList<ArrayList<HashMap<String, EntryInfo>>>> tableList; // a table holding the entries
    protected HashMap<String, ArrayList<GrammarRule>> lexicalMap; // a hashmap holding all lexical rules
    protected HashMap<String, ArrayList<GrammarRule>> unaryMap; // a hashmap holding all unary rules
    protected ArrayList<GrammarRule> binaryMap; // a hashmap holding all binary rules


    /**
     * the CKYParser constructor
     * @param rulesFile a string with the directory to our rules file
     * @param inputText a string with the directory to our input text file
     */
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

        // Parse the file
        parseFile(inputText);

        // Print the parse
        printParses();
    }


    /**
     * creates the CKY table
     * @param inputText a string with the directory to our input text file
     */
    public void parseFile(String inputText) {
        /* Start writing your parsing method. Create a new CKY table (two dimensions) and fill in the
            diagonals based on the lexical components. I would suggest writing a method that adds
            a constituent to your table (either in your entry class or as a standalone method). Print out
            the added constituents (either as they’re added or by printing out the table) and make sure
            everything is added appropriately. You can compare against your hand-written example(s).
             */
        try {

            // reads the file
            BufferedReader reader = new BufferedReader(new FileReader(inputText));
            ArrayList<ArrayList<HashMap<String, EntryInfo>>> table;
            String line;
            while ((line = reader.readLine()) != null) {

                // splits the line and creates an ArrayList
                String[] splitLine = line.split("\\s+");
                table = new ArrayList<>(splitLine.length);

                // populates the table
                populateTable(splitLine, table);
                if (addFirstDiagonal(splitLine, table)) {
                    addUpperTriangle(splitLine, table);
                    tableList.add(table);
                } else {
                    tableList.add(null);
                }
            }

            // closes the reader
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * a method that populates the table with HashMaps and nulls
     * @param splitLine an array representing the words of the line
     * @param table the table that is populated
     */
    public void populateTable(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {

        // adds an ArrayList with length of the splitLine array
        for (int i = 0; i < splitLine.length; i++) {
            table.add(new ArrayList<>(splitLine.length));

            // populates the parts of the table which are not used with nulls
            for (int x = 0; x < i; x++) {
                table.get(i).add(x, null);
            }

            // populates the parts of the table which are used with HashMaps
            for (int j = i; j < splitLine.length; j++) {
                table.get(i).add(j, new HashMap<>());
            }
        }
    }


    /**
     * checks each unary rule and stores the best version (based on weight) of each constituent
     * @param gr the grammar rule
     * @param ei the entry info
     * @param table the table
     * @param i the row number
     * @param j the column number
     */
    public void checkUnaryRules(GrammarRule gr, EntryInfo ei, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table, int i, int j) {
        if (unaryMap.containsKey(gr.getLhs())) {
            Reference unaryRuleRef = new Reference(gr.getLhs(), i, j);
            double newWeight;

            // goes through the unary rules
            for (GrammarRule rule : unaryMap.get(gr.getLhs())) {
                newWeight = rule.getWeight() + ei.getWeight();
                EntryInfo unaryInfo = new EntryInfo(newWeight, unaryRuleRef);

                // Only stores the best version (based on weight) of each constituent
                addIfBest(table, j, i, rule, newWeight, unaryInfo);
            }
        }
    }


    /**
     * populates the first diagonal of the table with an ArrayList containing the matching entries
     * @param splitLine an array representing the words in the line
     * @param table the table we are working with
     * @return returns true if there are no words that are out of vocabulary and false otherwise
     */
    public boolean addFirstDiagonal(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {
        int k = 0;

        // goes through the words and checks if lexical
        while (k < splitLine.length) {
            ArrayList<GrammarRule> lexList = lexicalMap.get(splitLine[k]);
            if (lexList != null) {
                for (GrammarRule gr : lexList) {

                    Reference wordRef = new Reference(gr.getRhs().get(0));
                    EntryInfo ei = new EntryInfo(gr.getWeight(), wordRef);

                    // check if the lhs of the new rule corresponds to rhs of any unary rule
                    checkUnaryRules(gr, ei, table, k, k);

                    // Only store the best version (based on weight) of each constituent
                    addIfBest(table, k, k, gr, gr.getWeight(), ei);
                }
            } else {
                return false;
            }
            k++;
        }
        return true;
    }


    /**
     * fills in the upper triangle of the table
     * @param splitLine an array of words representing the line
     * @param table the table which is worked on
     */
    public void addUpperTriangle(String[] splitLine, ArrayList<ArrayList<HashMap<String, EntryInfo>>> table) {

        // j corresponds to the second level of the 2D array
        for (int j = 1; j < splitLine.length; j++) {
            // i corresponds to the first level of the 2D array
            for (int i = j-1; i >= 0; i--) {
                for (int k = i+1; k <= j; k++) {
                    for (GrammarRule rule : binaryMap) {
                        // iterate through all of the binary rules and see if any of the rules match, that is RHS1 is
                        // in entry1 and RHS2 is in entry2
                        String rhs1 = rule.getRhs().get(0);
                        String rhs2 = rule.getRhs().get(1);

                        if (table.get(i).get(k-1).containsKey(rhs1) && table.get(k).get(j).containsKey(rhs2)) {
                            //if the entries match the binary rule, add the rule to the cell
                            Reference ref1 = new Reference(rhs1, i, k-1);
                            Reference ref2 = new Reference(rhs2, k, j);
                            double newWeight = rule.getWeight()
                                    + table.get(i).get(k-1).get(rhs1).getWeight()
                                    + table.get(k).get(j).get(rhs2).getWeight();
                            EntryInfo ei = new EntryInfo(newWeight, ref1, ref2);

                            // check if the lhs of the new rule corresponds to rhs of any unary rule
                            checkUnaryRules(rule, ei, table, i, j);

                            // Only store the best version (based on weight) of each constituent
                            addIfBest(table, j, i, rule, newWeight, ei);

                        }
                    }
                }
            }
        }
    }


    /**
     * adds the updated weight if it is "lighter" and puts the constituent in the appropriate cell
     * @param table the table which is worked on
     * @param j the column number
     * @param i the row number
     * @param rule the grammar rule
     * @param newWeight the new weight
     * @param ei the entry info
     */
    private void addIfBest(ArrayList<ArrayList<HashMap<String, EntryInfo>>> table, int j, int i, GrammarRule rule, double newWeight, EntryInfo ei) {
        if (table.get(i).get(j).containsKey(rule.getLhs())) {
            if (table.get(i).get(j).get(rule.getLhs()).getWeight() < newWeight) {
                // Put the constituent in the cell at (i, j)
                table.get(i).get(j).put(rule.getLhs(), ei);
            }
        } else {
            // Put the constituent in the cell at (i, j)
            table.get(i).get(j).put(rule.getLhs(), ei);
        }
    }


    /**
     * prints the parses
     */
    public void printParses() {

        // goes through every ArrayList in the table
        for (ArrayList<ArrayList<HashMap<String, EntryInfo>>> table : tableList) {
            if (table != null) {
                int j = table.get(0).size() - 1;

                // adds "S" to the beginning of the parses and calls AddEntry method
                String parseString = "(S" + addEntry(table, 0, j, "S") + "\t"
                        + table.get(0).get(j).get("S").getWeight();
                System.out.println(parseString);
            } else {
                System.out.println("NULL");
            }
        }
    }


    /**
     * recursively goes through the entries using their refrences and generates the parse string
     * @param table the table
     * @param i row number
     * @param j column number
     * @param constituent the constituent string
     * @return the parse string
     */
    public String addEntry(ArrayList<ArrayList<HashMap<String, EntryInfo>>> table, int i, int j, String constituent) {

        // gets the references from the entry using the row and column numbers
        EntryInfo entry = table.get(i).get(j).get(constituent);
        Reference ref1 = entry.getRef1();
        Reference ref2 = entry.getRef2();

        // checks if lexical and if so adds the constituent to the string
        if (ref1.isLexical()) {
            return " " + ref1.getCons() + ")";
        } else {

            // if not lexical, adds the constituents to the string and calls the function on the references for
            // the next entries
            if (ref2 != null) {
                if (ref2.isLexical()) {
                    return " (" + ref1.getCons() +
                            addEntry(table, ref1.getI(), ref1.getJ(), ref1.getCons())
                            + ref2.getCons() + ")";
                } else {
                    return " (" + ref1.getCons()
                            + addEntry(table, ref1.getI(), ref1.getJ(), ref1.getCons())
                            + " (" + ref2.getCons()
                            + addEntry(table, ref2.getI(), ref2.getJ(), ref2.getCons()) + ")";
                }
            }
            return " (" + ref1.getCons()
                    + addEntry(table, ref1.getI(), ref1.getJ(), ref1.getCons())+ ")";
        }
    }

    public static void main (String args[]) {
        String rulesFile = "/Users/ezraford/Desktop/School/CS 159/NLP-CKYParser/assign4-starter/data/full.pcfg";
        String inputFile = "/Users/ezraford/Desktop/School/CS 159/NLP-CKYParser/assign4-starter/data/test.sentences";
        String outputFile = "/Users/ezraford/Desktop/School/CS 159/NLP-CKYParser/assign4-starter/data/test.sentences.parsed";

        CKYParser ckyParser = new CKYParser(rulesFile, inputFile);
    }
}
