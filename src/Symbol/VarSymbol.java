package Symbol;

import token.Token;

/**变量和常量统称为变量罢😅，通过isConst区分。实在找不到一个上层概念来统称了 */
public class VarSymbol extends Symbol {
    private VarType varType;
    private boolean isConst;
    private boolean isArray;
    private int length = 0;
    /**数组维数，非数组可设为0 */
    private int dim;

    public VarSymbol(Token identToken, VarType varType, boolean isConst, boolean isArray) {
        super(identToken);
        this.varType = varType;
        this.isConst = isConst;
        this.isArray = isArray;
    }

    public VarType getVarType() {
        return varType;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return word + " " + (isConst ? "Const" : "") + varType.toString() + (isArray ? "Array" : "");
    }
}
