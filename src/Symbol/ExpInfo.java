package Symbol;

public class ExpInfo {
    public VarType varType;
    public int regIndex;
    public ExpInfo(VarType varType, int regIndex) {
        this.varType = varType;
        this.regIndex = regIndex;
    }
    public ExpInfo() {}
}
