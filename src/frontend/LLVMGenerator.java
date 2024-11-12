package frontend;

import java.util.HashMap;
import java.util.Map;

import Symbol.ExpInfo;
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

    private final static Map<TokenType, VarType> tokentype2VarTypeMap = new HashMap<>() {{
        put(TokenType.INTTK, VarType.Int);
        put(TokenType.CHARTK, VarType.Char);
    }};

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

    public int makeCalculate(Token calculateToken, LLVMToken llvmToken1, LLVMToken llvmToken2) {
        TokenType calculateType = calculateToken.getType();
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calculateType) + " i32 " + llvmToken1.toString() + ", " + llvmToken2.toString();
        System.out.println(printString);
        return dstReg;
    }

    public int makeCalculate(Token calculateToken, boolean isRegPtr1, int param1, boolean isRegPtr2, int param2) {
        TokenType calculateType = calculateToken.getType();
        // if (isRegPtr1 == true) {
        //     param1 = makeLoadStmt(param1, VarType.Int);
        // }
        // if (isRegPtr2 == true) {
        //     param2 = makeLoadStmt(param2, VarType.Int);
        // }
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calculateType) + " i32 " + (isRegPtr1 ? "%" : "") + param1 + ", " + (isRegPtr2 ? "%" : "") + param2;
        System.out.println(printString);
        return dstReg;
    }

    public int makeStoreImm(int imm, VarType varType) {
        int dstRegPtr;
        dstRegPtr = makeAllocaStmt(varType);
        makeStoreImmStmt(imm, dstRegPtr, varType);
        return dstRegPtr;
    }

    //此处中间代码冗余严重 记得进行优化
    /**@return 分配寄存器编号 */
    private int makeAllocaStmt(VarType varType) {
        int srcRegPtr = regIndex;
        String printString = getSpace() + getReg() + " = alloca " + varType2LengthMap.get(varType);
        System.out.println(printString);
        return srcRegPtr;
    }

    private void makeStoreImmStmt(int imm, int dstReg, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + imm + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(dstReg);
        System.out.println(printString);
    }

    public void makeStoreRegStmt(int srcReg, int dstRegPtr, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(dstRegPtr);
        System.out.println(printString);
    }

    public void makeStoreRegStmt(int srcReg, String dstRegPtr, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + "* " + varName2Reg(dstRegPtr);
        System.out.println(printString);
    }

    public void makeStoreRegStmt(int srcReg, ExpInfo dstExpInfo) {
        VarType varType = dstExpInfo.varType;
        boolean isGlobal = dstExpInfo.globalVarName != null;
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + "* " + getRegSymbol(isGlobal) + dstExpInfo.getReg();
        System.out.println(printString);
    }

    public int makeLoadStmt(int srcRegPtr, VarType varType) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = load " + varType2LengthMap.get(varType) + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(srcRegPtr);
        System.out.println(printString);
        return dstReg;
    }

    public int makeLoadStmt(String varName, VarType varType) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = load " + varType2LengthMap.get(varType) + ", " + varType2LengthMap.get(varType) + "* " + varName2Reg(varName);
        System.out.println(printString);
        return dstReg;
    }

    private FuncType funcType = FuncType.Int;

    public void setFuncType(FuncType funcType) {
        this.funcType = funcType;
    }

    //我们在return语句中知晓其是否有返回值，故这一步骤不在此处判断
    public void makeReturnRegStmt(int srcReg) {
        
        String printString = getSpace() + "ret ";
        printString += funcType2LengthMap.get(funcType) + " ";
        printString += index2Reg(srcReg);
        System.out.println(printString);
    }

    public void makeReturnImmStmt(int imm) {
        String printString = getSpace() + "ret ";
        printString += funcType2LengthMap.get(funcType) + " ";
        printString += imm;
        System.out.println(printString);
    }

    public void makeReturnStmt() {
        String printString = getSpace() + "ret void";
        System.out.println(printString);
    }

    //需要保证在每次调用makeVarDeclareStmt前调用setVarType
    private boolean isDeclVarGlobal;

    public void setIsDeclVarGlobal(boolean isGlobal) {
        isDeclVarGlobal = isGlobal;
    }
    private VarType declareVarType;
    
    public void setVarType(Token token) {
        declareVarType = tokentype2VarTypeMap.get(token.getType());
    }

    public int makeDeclStmt(String name, Integer value) {
        if (isDeclVarGlobal == true) {
            makeGlobalDeclStmt(name, value == null ? 0 : value);
            return 0;
        } else {
            return makeLocalDeclStmt(value);
        }
    }

    public int makeDeclStmt(int srcReg) {
        int dstRegPtr = makeAllocaStmt(declareVarType);
        makeStoreRegStmt(srcReg, dstRegPtr, declareVarType);
        return dstRegPtr;
    }

    private void makeGlobalDeclStmt(String name, int value) {
        String printString = getSpace() + "@" + name + " = dso_local global " + varType2LengthMap.get(declareVarType) + " " + value;
        System.out.println(printString);
    }

    private int makeLocalDeclStmt(Integer value) {
        int reg = makeAllocaStmt(declareVarType);
        if (value != null) {
            makeStoreImmStmt(value, reg, declareVarType);
        }
        return reg;
    }

    public int makeCallFunctionStmt(String funcName, FuncType funcType) {
        int dstReg = regIndex;
        String printString;
        if (funcType.equals(FuncType.Void) == true) {
            printString = getSpace() + "call void @" + funcName + "()";
            dstReg = 0;
        } else {
            printString = getSpace() + getReg() + " = call " + funcType2LengthMap.get(funcType) + " @" + funcName + "()";
        }
        System.out.println(printString);
        return dstReg;
    }

    //为了保证读取regIndex时自增
    private String getReg() {
        return "%" + regIndex++;
    }

    private String getSpace() {
        return " ".repeat(blockDeep * 4);
    }

    private String index2Reg(int regIndex) {
        return "%" + regIndex;
    }

    private String varName2Reg(String varName) {
        return "@" + varName;
    }

    private String getRegSymbol(boolean isGlobal) {
        return isGlobal ? "@" : "%";
    }

}
