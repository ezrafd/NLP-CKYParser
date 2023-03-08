package nlp.parser;

/** Tal + Ezra
 * EntryInfo is a custom class that holds the weight of the constituent as a double and a reference to the constituents
 * that it came from as two Reference objects ref1 and ref2.
 */

public class EntryInfo {
    protected double weight; //the constituent's weight
    protected Reference ref1; //reference to previous constituent
    protected Reference ref2; //reference to previous constituent


    /**
     * constructs an EntryInfo with two references and a weight
     * @param weight - the weight of the constituent
     * @param ref1 - reference to the left part of the right hand side of the constituent
     * @param ref2 - reference to the right part of the right hand side of the constituent
     */
    public EntryInfo (Double weight, Reference ref1, Reference ref2){
        this.weight = weight;
        this.ref1 = ref1;
        this.ref2 = ref2;
    }

    /**
     * constructs an EntryInfo with one references and a weight (for unary and lexical rules)
     * @param weight
     * @param ref1
     */
    public EntryInfo (Double weight, Reference ref1) {
        this.weight = weight;
        this.ref1 = ref1;
        this.ref2 = null;
    }

    /**
     * returns the weight
     * @return weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * returns ref1
     * @return
     */
    public Reference getRef1() {
        return ref1;
    }

    /**
     * returns ref 2
     * @return
     */
    public Reference getRef2() {
        return ref2;
    }

    /**
     * Prints the reference
     * @return reference information
     */
    @Override
    public String toString() {
        return ("[weight: " + weight + " ref1: " + ref1 + " ref2: " + ref2 +"]");
    }
}
