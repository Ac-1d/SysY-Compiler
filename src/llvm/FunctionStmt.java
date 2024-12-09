package llvm;

import java.util.Arrays;
import java.util.List;

import Symbol.FuncParam;
import Symbol.FuncType;
import frontend.LLVMGenerator;

public class FunctionStmt extends LLVMStmt {
    // head
    private FuncType funcType;
    private String funcName;
    private List<FuncParam> funcParams; 
    // body
    private List<Stmt> stmts;

    LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();

    public FunctionStmt(FuncType funcType, String funcName, FuncParam... funcParams) {
        this.funcType = funcType;
        this.funcName = funcName;
        this.funcParams = Arrays.asList(funcParams);
    }

    public void print() {
        String printString = "define dso_local ";
        printString += funcType2LengthMap.get(funcType) + " @" + funcName + "(";
        boolean isFirst = true;
        for (FuncParam funcParam : funcParams) {
            if (isFirst == false) {
                printString += ", ";
            }
            if (isFirst) isFirst = false;
            printString += varType2LengthMap.get(funcParam.getVarType()) + " " + llvmGenerator.getReg();
        }
        printString += "){";
        System.out.println(printString);
    }
}
