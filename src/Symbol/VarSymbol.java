package Symbol;

import java.util.ArrayList;
import java.util.List;

import token.Token;

/**变量和常量统称为变量罢😅，通过isConst区分。实在找不到一个上层概念来统称了 */
public class VarSymbol extends Symbol {
    private VarType varType;
    private boolean isConst;
    private boolean isArray;
    private int length = 1;
    private List<Integer> valueList = new ArrayList<>();
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

    public boolean hasValue() {
        return !valueList.isEmpty();
    }
    
    public void setValue() {
        for (int i = 0; i < length; i++) {
            valueList.add(0);
        }
    }

    public void setValue(Integer value) {
        valueList.add(value);
    }

    public void setValue(List<ExpInfo> expInfos) {
        for (int i = 0; i < length; i++) {
            if (i < expInfos.size()) {
                valueList.add(expInfos.get(i).getValue());
            } else {
                valueList.add(0);
            }
        }
    }

    public void setValue(String constr) {
        for (int i = 0; i < length; i++) {
            if (i < constr.length()) {
                valueList.add((int) constr.charAt(i));
            } else {
                valueList.add(0);
            }
        }
    }

    public int getValue() {
        return getValue(0);
    }

    public int getValue(int index) {
        return valueList.get(index);
    }

    @Override
    public String toString() {
        return word + " " + (isConst ? "Const" : "") + varType.toString() + (isArray ? "Array" : "");
    }
}
