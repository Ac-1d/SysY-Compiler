package Symbol;

public class ExpInfo {
    public VarType varType;
    public int regIndex;
    public String globalVarName;
    private boolean isGlobal;
    public ExpInfo(VarType varType, int regIndex) {
        this.varType = varType;
        this.regIndex = regIndex;
    }
    public void setExpInfo(Symbol symbol) {
        isGlobal = symbol.isGlobal();
        if (symbol.isGlobal() == true) {
            globalVarName = symbol.getWord();
        } else {
            regIndex = symbol.getReg();
        }
        varType = ((VarSymbol) symbol).getVarType();
    }
    public ExpInfo() {}

    public String getReg() {
        return isGlobal ? globalVarName : regIndex + "";
    }
}
