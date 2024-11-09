package frontend;

import java.util.HashMap;
import java.util.Map;

import Symbol.FuncType;
import Symbol.VarType;
import Symbol.LLVMToken.LLVMToken;
import token.Token;
import token.TokenType;

public class LLVMGenerator {
    private static final LLVMGenerator instance = new LLVMGenerator();
    private LLVMGenerator() {}
    public static LLVMGenerator getInstance() {
        return instance;
    }

    private final static Map<VarType, String> varType2LengthMap = new HashMap<>() {{
        put(VarType.Char, "i8");
        put(VarType.Int, "i32");
    }};

    private final static Map<FuncType, String> funcType2LengthMap = new HashMap<>() {{
        put(FuncType.Char, "i8");
        put(FuncType.Int, "i32");
        put(FuncType.Void, "void");
    }};

    private final static Map<TokenType, String> tokenType2CalculateTypeMap = new HashMap<>() {{
        put(TokenType.PLUS, "add nsw");
        put(TokenType.MINU, "sub nsw");
        put(TokenType.MULT, "mul nsw");
        put(TokenType.DIV, "sdiv");
        put(TokenType.MOD, "srem");
    }};

    private int blockDeep = 0;
    private int regIndex = 1;

    public void enterBlock() {
        blockDeep++;
        regIndex = 1;
    }

    public void exitBlock() {
        blockDeep--;
    }

    public void makeFunction(FuncType funcType, String funcName) {
        String printString = getSpace() + "define dso_local ";
        printString += funcType2LengthMap.get(funcType) + " ";
        printString += "@";
        printString += funcName;
        printString += "(){";//TOBECONTINUE
        System.out.println(printString);
        enterBlock();
    }
    
    public void makeFunctionEnd() {
        exitBlock();
        String printString = getSpace() + "}";
        System.out.println(printString);
    }

    public int makeCalculate(Token calcuateToken, LLVMToken llvmToken1, LLVMToken llvmToken2) {
        TokenType calcuateType = calcuateToken.getType();
        int regIndex = this.regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calcuateType) + " i32 " + llvmToken1.toString() + ", " + llvmToken2.toString();
        System.out.println(printString);
        return regIndex;
    }

    public int makeStoreImm(int imm, VarType varType) {
        int regIndex;
        regIndex = makeAllocaStmt(varType);
        makeStoreStmt(imm, regIndex, varType);
        regIndex = makeLoadStmt(regIndex, varType);
        return regIndex;
    }

    //大胆一点，暂时假设Alloca与Store只会合用，仅对外暴露makeStoreImm/makeStoreReg
    //此处中间代码冗余严重 记得进行优化
    /**@return 分配寄存器编号 */
    private int makeAllocaStmt(VarType varType) {
        int regIndex = this.regIndex;
        String printString = getSpace() + getReg() + "= alloca " + varType2LengthMap.get(varType);
        System.out.println(printString);
        return regIndex;
    }

    private void makeStoreStmt(int imm, int regIndex, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + imm + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(regIndex);
        System.out.println(printString);
    }

    private int makeLoadStmt(int loadRegIndex, VarType varType) {
        int regIndex = this.regIndex;
        String printString = getSpace() + getReg() + "= load " + varType2LengthMap.get(varType) + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(loadRegIndex);
        System.out.println(printString);
        return regIndex;
    }

    //我们在return语句中知晓其是否有返回值，故这一步骤不在此处判断
    public void makeReturnStmt(int regIndex, VarType varType) {
        String printString = getSpace() + "ret ";
        printString += varType2LengthMap.get(varType) + " ";
        printString += index2Reg(regIndex);
        System.out.println(printString);
    }

    public void makeReturnStmt() {
        String printString = getSpace() + "ret void";
        System.out.println(printString);
    }

    //为了保证读取regIndex时自增
    private String getReg() {
        return "%" + regIndex++ + " ";
    }

    private String getSpace() {
        return " ".repeat(blockDeep * 4);
    }

    private String index2Reg(int regIndex) {
        return "%" + regIndex;
    }

}
