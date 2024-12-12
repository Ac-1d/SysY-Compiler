package Symbol;

public class FuncRParam extends FuncParam{
    private ExpInfo expInfo;
    public FuncRParam(ExpInfo expInfo, boolean isArray) {
        super(expInfo.varType, isArray);
        this.expInfo = expInfo;
    }

    public FuncRParam(ExpInfo expInfo, FuncParam funcParam) {
        super(funcParam.getVarType(), funcParam.isArray());
        this.expInfo = expInfo;
    }

    public ExpInfo getExpInfo() {
        return expInfo;
    }
}
