package nlp.parser;

/*
constituent String and the int i and int j corresponding to the row and column of the cell containing the constituent
being referenced. It will have a secondary constructor with a different signature for if the rule is lexical, which
will set both int i and int j to null, then set an isLexical boolean instance variable to True, which is normally set
to false. It will set the String equal to the word which it came from. If the rule is lexical or unary, ref2
will be null.
 */

public class Reference {
    protected String cons; //the constituent string
    protected int i; //row number
    protected int j; //col number
    protected boolean is_lexical; //indicator if the Reference is a lexical rule


    public Reference (String cons, int i, int j){
        this.cons = cons;
        this.i = i;
        this.j = j;
        is_lexical = false;
    }

    public Reference (String cons){
        this.cons = cons;
        is_lexical = true;
    }
}
