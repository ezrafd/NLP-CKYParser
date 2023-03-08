package nlp.parser;

public class EntryInfo {
    protected double weight; //the constituent's weight
    protected Reference ref1; //reference to previous constituent
    protected Reference ref2; //reference to previous constituent

    public EntryInfo (Double weight, Reference ref1, Reference ref2){
        this.weight = weight;
        this.ref1 = ref1;
        this.ref2 = ref2;
    }

    public EntryInfo (Double weight, Reference ref1) {
        this.weight = weight;
        this.ref1 = ref1;
        this.ref2 = null;
    }

    public double getWeight() {
        return weight;
    }

    public Reference getRef1() {
        return ref1;
    }

    public Reference getRef2() {
        return ref2;
    }

    @Override
    public String toString() {
        return ("[weight: " + weight + " ref1: " + ref1 + " ref2: " + ref2 +"]");
    }
}
