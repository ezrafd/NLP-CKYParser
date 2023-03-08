package nlp.parser;

/** Tal + Ezra
 * Reference is a custom class which contains and allows you to get constituent String and the int i and int j
 * corresponding to the row and column of the cell containing the constituent being referenced.
 */

public class Reference {
    protected String cons; //the constituent string
    protected int i; //row number
    protected int j; //col number
    protected boolean is_lexical; //indicator if the Reference is a lexical rule

    /**
     * constructs an EntryInfo that gets a row and column numbers and is not lexical
     * @param cons the constituent string
     * @param i the row number
     * @param j the column number
     */
    public Reference (String cons, int i, int j){
        this.cons = cons;
        this.i = i;
        this.j = j;
        is_lexical = false;
    }

    /**
     * constructs an EntryInfo that does not get a row and column numbers and is lexical
     * @param cons the constituent string
     */
    public Reference (String cons){
        this.cons = cons;
        is_lexical = true;
    }

    /**
     *
     * @return row number (i)
     */
    public int getI() {
        return i;
    }

    /**
     *
     * @return column number (j)
     */
    public int getJ() {
        return j;
    }

    /**
     *
     * @return the constituent string
     */
    public String getCons() {
        return cons;
    }

    /**
     *
     * @return true if lexical, false if not
     */
    public boolean isLexical() {
        return is_lexical;
    }

    /**
     *
     * @return the constituent followed by the row and column numbers respectively
     */
    @Override
    public String toString() {
        return cons + " (" + i + ", " + j + ")";
    }
}
