package Symbol;

public class FuncParam {
    private VarType varType;
    private boolean isArray;

    public FuncParam(VarType varType, boolean isArray) {
        this.varType = varType;
        this.isArray = isArray;
    }

    public VarType getVarType() {
        return varType;
    }

    public boolean getIsArray() {
        return isArray;
    }
}
