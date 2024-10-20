package Symbol;

import java.util.ArrayList;
import java.util.List;

import token.Token;

public class FuncSymbol extends Symbol {
    private FuncType funcType;
    private List<FuncParam> funcParamsList = new ArrayList<>();

    public FuncSymbol(Token identToken, FuncType funcType) {
        super(identToken);
        this.funcType = funcType;
    }

    public void addFuncParam(FuncParam funcParam) {
        funcParamsList.add(funcParam);
    }

    public List<FuncParam> getFuncParamsList() {
        return funcParamsList;
    }

    public int getParamsNum() {
        return funcParamsList.size();
    }

    public FuncType getFuncType() {
        return funcType;
    }

    @Override
    public String toString() {
        return word + " " + funcType.toString() + "Func"; 
    }
}
