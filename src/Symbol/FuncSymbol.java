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

    public int getParamsNum() {
        return funcParamsList.size();
    }

    @Override
    public String toString() {
        return word + " " + funcType.toString() + "Func"; 
    }
}
