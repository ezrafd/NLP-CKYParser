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
}
