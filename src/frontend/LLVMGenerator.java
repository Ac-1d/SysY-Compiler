package frontend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Symbol.ExpInfo;
import Symbol.FuncParam;
import Symbol.FuncRParam;
import Symbol.FuncType;
import Symbol.VarType;
import config.Config;
import token.Token;
import token.TokenType;

public class LLVMGenerator {
    private static final LLVMGenerator instance = new LLVMGenerator();
    private LLVMGenerator() {}
    public static LLVMGenerator getInstance() {
        return instance;
    }

    private List<String> printStrings = new ArrayList<>();

    public void print() {
        for (String string : printStrings) {
            System.out.println(string);
        }
    }

    public void init() {
        strconTokenNum = 0;
    }

    private int strconTokenNum = 0;
    private Map<Integer, Integer> strNum2LengthMap = new HashMap<>();

    public int getStrconTokenNum() {
        return strconTokenNum++;
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
        put(TokenType.AND, "and");
        put(TokenType.OR, "or");

    }};

    private int blockDeep = 0;
    private int regIndex = 0;

    public void enterBlock() {
        blockDeep++;
        regIndex++;
    }

    public void exitBlock() {
        blockDeep--;
        regIndex = 0;
    }

    public void makeFunctionStmt(FuncType funcType, String funcName, FuncParam... funcParams) {
        this.funcType = funcType;
        String printString = getSpace() + "define dso_local ";
        printString += funcType2LengthMap.get(funcType) + " @" + funcName + "(";
        for (int i = 0; i < funcParams.length; i++) {
            FuncParam funcParam = funcParams[i];
            if (i > 0) {
                printString += ", ";
            }
            printString += varType2LengthMap.get(funcParam.getVarType()) + " " + getReg();
        }
        printString += "){";
        printStrings.add(printString);
        // for label
        currentLabel.clear();
        currentLabel.push(regIndex);
        enterBlock();
    }

    public void makeFunctionStmt(FuncType funcType, String funcName, List<FuncParam> funcParams) {
        FuncParam[] funcParams2 = new FuncParam[funcParams.size()];
        for (int i = 0; i < funcParams.size(); i++) {
            funcParams2[i] = funcParams.get(i);
        }
        makeFunctionStmt(funcType, funcName, funcParams2);
    }
    
    public void makeFunctionEnd() {
        exitBlock();
        String printString = getSpace() + "}";
        printStrings.add(printString);
    }
    
    public int makeCalculateStmt(Token calculateToken, ExpInfo expInfo1, ExpInfo expInfo2) {
        TokenType calTokenType = calculateToken.getType();
        if (expInfo1.varType != VarType.Int) {
            expInfo1 = makeTransStmt(expInfo1);
        }
        if (expInfo2.varType != VarType.Int) {
            expInfo2 = makeTransStmt(expInfo2);
        }
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calTokenType) + " i32 " + expInfo1.getCalculateParam() + ", " + expInfo2.getCalculateParam();
        printStrings.add(printString);
        return dstReg;
    }

    public int makeLogicCalculateStmt(Token calculateToken, ExpInfo expInfo1, ExpInfo expInfo2) {
        if (expInfo1.varType != VarType.Int) {
            expInfo1 = makeTransStmt(expInfo1);
        }
        if (expInfo2.varType != VarType.Int) {
            expInfo2 = makeTransStmt(expInfo2);
        }
        int dstReg = regIndex;
        
        return dstReg;
    }

    /**@return 分配寄存器编号 */
    private int makeAllocaStmt(VarType varType) {
        int srcRegPtr = regIndex;
        String printString = getSpace() + getReg() + " = alloca " + varType2LengthMap.get(varType);
        printStrings.add(printString);
        return srcRegPtr;
    }

    private void makeStoreImmStmt(int imm, int dstReg, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + imm + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(dstReg);
        printStrings.add(printString);
    }

    private void makeStoreRegStmt(int srcReg, ExpInfo dstExpInfo) {
        VarType varType = dstExpInfo.varType;
        boolean isGlobal = dstExpInfo.globalVarName != null;
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + "* " + getRegSymbol(isGlobal) + dstExpInfo.getReg();
        printStrings.add(printString);
    }

    public void makeStoreStmt(ExpInfo srcExpInfo, ExpInfo dstExpInfo) {
        VarType varType = dstExpInfo.varType;
        if (srcExpInfo.varType != varType) {
            srcExpInfo = makeTransStmt(srcExpInfo);
        }
        boolean isGlobal = dstExpInfo.globalVarName != null;
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + srcExpInfo.getCalculateParam() + ", " + varType2LengthMap.get(varType) + "* " + getRegSymbol(isGlobal) + dstExpInfo.getReg();
        printStrings.add(printString);
    }

    public int makeLoadStmt(ExpInfo expInfo) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = load " + varType2LengthMap.get(expInfo.varType) + ", " + varType2LengthMap.get(expInfo.varType) + "* " + (expInfo.isGlobal ? "@" : "%") + expInfo.getReg();
        printStrings.add(printString);
        return dstReg;
    }

    private FuncType funcType = FuncType.Int;

    //我们在return语句中知晓其是否有返回值，故这一步骤不在此处判断
    public void makeReturnRegStmt(ExpInfo expInfo) {
        int srcReg;
        String printString = getSpace() + "ret " + funcType2LengthMap.get(funcType) + " ";
        if (expInfo.varType.toString() != funcType.toString() && expInfo.isConst() == false) {
            expInfo = makeTransStmt(expInfo);
        }
        if (expInfo.getValue() != null) {
            printString += expInfo.getValue();
        } else {
            srcReg = expInfo.regIndex;
            printString += index2Reg(srcReg);
        }
        printStrings.add(printString);
        getReg();
    }

    public void makeReturnStmt() {
        String printString = getSpace() + "ret void";
        printStrings.add(printString);
        getReg();
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
        makeStoreRegStmt(srcReg,new ExpInfo(declareVarType, dstRegPtr));
        return dstRegPtr;
    }

    private void makeGlobalDeclStmt(String name, int value) {
        String printString = getSpace() + "@" + name + " = dso_local global " + varType2LengthMap.get(declareVarType) + " " + value;
        printStrings.add(printString);
    }

    private int makeLocalDeclStmt(Integer value) {
        int reg = makeAllocaStmt(declareVarType);
        if (value != null) {
            makeStoreImmStmt(value, reg, declareVarType);
        }
        return reg;
    }

    public int makeCallFunctionStmt(String funcName, FuncType funcType, FuncRParam... funcRParams) {
        int dstReg = 0;
        String printString;
        if (funcType.equals(FuncType.Void) == true) {
            printString = getSpace() + "call void";
        } else {
            dstReg = regIndex;
            printString = getSpace() + getReg() + " = call " + funcType2LengthMap.get(funcType);

        }
        printString += " @" + funcName + "(";
        for (int i = 0; i < funcRParams.length; i++) {
            if (i > 0) {
                printString += ", ";
            }
            FuncRParam funcRParam = funcRParams[i];
            printString += varType2LengthMap.get(funcRParam.getVarType()) + " " + funcRParam.getExpInfo().getCalculateParam();
        }
        printString += ")";
        printStrings.add(printString);
        return dstReg;
    }

    public void makeCallPutstrStmt(int strNum) {
        int length = strNum2LengthMap.get(strNum);
        String printString = getSpace() + "call void @putstr(i8* getelementptr inbounds ([" + length + " x i8], [" + length + " x i8]* @.str." + strNum + ", i64 0, i64 0))";
        printStrings.add(printString);
    }

    public ExpInfo makeTransStmt(ExpInfo expInfo) {//要修改 还有i1😅
        int dstReg = regIndex;
        VarType srcVarType = expInfo.varType;
        int srcReg = expInfo.regIndex;
        VarType dstVarType = srcVarType.equals(VarType.Int) ? VarType.Char : VarType.Int;
        String printString = getSpace() + getReg() + " = " + (srcVarType.equals(VarType.Int) ? "trunc" : "zext") + " " + varType2LengthMap.get(srcVarType) + " " + index2Reg(srcReg) + " to " + varType2LengthMap.get(dstVarType);
        printStrings.add(printString);
        expInfo.setReg(dstReg);
        expInfo.varType = dstVarType;
        return expInfo;
    }

    public void makeConstrStmt(String constr, int num, int length) {
        //不需要缩进
        String printString = "@.str." +  num + " = private unnamed_addr constant [" + length + " x i8] c\"" + dealConstr(constr, length) + "\"";
        strNum2LengthMap.put(num, length);
        Config.llvmData();
        System.out.println(printString);
        Config.continueLLVMText();
    }

    public int makeIfStmt(ExpInfo expInfo) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = icmp ne " + varType2LengthMap.get(expInfo.varType) + " " + expInfo.getCalculateParam() + ", 0";
        printStrings.add(printString);
        printString = "br in" + dstReg;
        printStrings.add(printString);
        return dstReg;
    }

    private Deque<Integer> currentLabel = new ArrayDeque<>();

    public int setLabel() {
        int label = regIndex;
        // currentLabel.add(label);
        String printString = regIndex + ":  ; preds = %";
        getReg();
        printString += currentLabel.peek();
        currentLabel.push(label);
        printStrings.add(printString);
        return label;
    }

    public void quitIfStmt() {
        String printString = "br out";
        printStrings.add(printString);
        currentLabel.pop();
    }

    public void setBr(int label1, int label2) {
        for(int i = printStrings.size() - 1; i >= 0; i--) {
            String str = printStrings.get(i);
            String tag = "br in";
            if (str.startsWith(tag)) {
                String reg = str.substring(tag.length());
                printStrings.set(i, getSpace() + "br i1 %" + reg + ", label %" + label1 + ", label %" + label2);
                break;
            }
        }
        int num = 2;
        for(int i = printStrings.size() - 1; i >= 0; i--) {
            String str = printStrings.get(i);
            String tag = "br out";
            if (str.startsWith(tag)) {
                printStrings.set(i, getSpace() + "br label %" + regIndex);
                num--;
                if (num == 0) {
                    break;
                }
            }
        }
        printStrings.add(regIndex + ":  ; preds = %" + label1 + ", %" + label2);
        getReg();
    }

    public void setBr(int label) {
        for(int i = printStrings.size() - 1; i >= 0; i--) {
            String str = printStrings.get(i);
            String tag = "br in";
            if (str.startsWith(tag)) {
                String reg = str.substring(tag.length());
                printStrings.set(i, getSpace() + "br i1 %" + reg + ", label %" + label + ", label %" + regIndex);
                break;
            }
        }
        int num = 1;
        for(int i = printStrings.size() - 1; i >= 0; i--) {
            String str = printStrings.get(i);
            String tag = "br out";
            if (str.startsWith(tag)) {
                printStrings.set(i, getSpace() + "br label %" + regIndex);
                num--;
                if (num == 0) {
                    break;
                }
            }
        }
        printStrings.add(regIndex + ":  ; preds = %" + label + ", %" + currentLabel.peek());
        getReg();
    }

    private String dealConstr(String constr, int length) {
        constr += "\\00".repeat(length - countConstrLength(constr));
        constr = constr.replace("\\a", "\\07");
        constr = constr.replace("\\b", "\\08");
        constr = constr.replace("\\t", "\\09");
        constr = constr.replace("\\n", "\\0A");
        constr = constr.replace("\\v", "\\0B");
        constr = constr.replace("\\f", "\\0C");
        constr = constr.replace("\\\"", "\\22");
        constr = constr.replace("\\\'", "\\27");
        constr = constr.replace("\\\\", "\\5c");
        return constr;
    }

    private static int countConstrLength(String constr) {
        int length = 0;
        int state = 0;
        for (char c : constr.toCharArray()) {
            switch (state) {
                case 0://common
                    length++;
                    if (c == '\\') {
                        state = 1;
                    }
                    break;
                case 1:// '\\'
                    state = 0;
                    break;
                default:
                    break;
            }
        }
        return length;
    }

    //为了保证读取regIndex时自增
    public String getReg() {
        return "%" + regIndex++;
    }

    private String getSpace() {
        return " ".repeat(blockDeep * 4);
    }

    private String index2Reg(int regIndex) {
        return "%" + regIndex;
    }

    private String getRegSymbol(boolean isGlobal) {
        return isGlobal ? "@" : "%";
    }

}
