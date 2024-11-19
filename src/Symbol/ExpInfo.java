package Symbol;

/**存储变量类型、寄存器编号/全局变量名称 */
public class ExpInfo {
    public VarType varType;
    public int regIndex;
    public String globalVarName;
    public boolean isGlobal = false;
    private Integer value;
    public ExpInfo(VarType varType, int regIndex) {
        this.varType = varType;
        this.regIndex = regIndex;
    }
    public ExpInfo(int value) {
        this.value = value;
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

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getCalculateParam() {
        return value == null ? "%" + getReg() : value + "";
    }

    public String getReg() {
        return isGlobal ? globalVarName : regIndex + "";
    }

    public void setReg(int reg) {
        isGlobal = false;
        globalVarName = null;
        value = null;
        regIndex = reg;
    }

    public boolean isConst() {
        return value != null;
    }

    public void setVarType(FuncType funcType) {
        if (funcType.equals(FuncType.Int)) {
            varType = VarType.Int;
        } else if (funcType.equals(FuncType.Char)) {
            varType = VarType.Char;
        }
    }
}
