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
import token.Token;
import token.TokenType;

public class LLVMGenerator {
    private static final LLVMGenerator instance = new LLVMGenerator();
    private LLVMGenerator() {}
    public static LLVMGenerator getInstance() {
        return instance;
    }

    private List<String> printConstrs = new ArrayList<>();
    private List<String> printStrings = new ArrayList<>();

    public void print() {
        for (String string : printConstrs) {
            System.out.println(string);
        }
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
        put(TokenType.LSS, "icmp slt");
        put(TokenType.LEQ, "icmp sle");
        put(TokenType.GRE, "icmp sgt");
        put(TokenType.GEQ, "icmp sge");
        put(TokenType.EQL, "icmp eq");
        put(TokenType.NEQ, "icmp ne");
        put(TokenType.NOT, "icmp eq");
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
            printString += varType2LengthMap.get(funcParam.getVarType()) + (funcParam.isArray() ? "*" : "") + " " + getReg();
        }
        printString += "){";
        printStrings.add(printString);
        enterBlock();
    }

    public void makeFunctionStmt(FuncType funcType, String funcName, List<FuncParam> funcParams) {
        FuncParam[] funcParams2 = new FuncParam[funcParams.size()];
        for (int i = 0; i < funcParams.size(); i++) {
            funcParams2[i] = funcParams.get(i);
        }
        makeFunctionStmt(funcType, funcName, funcParams2);
    }
    
    public void makeFunctionEnd(FuncType funcType) {
        if (funcType.equals(FuncType.Void)) {
            makeReturnStmt();
        }
        exitBlock();
        String printString = getSpace() + "}";
        printStrings.add(printString);
    }
    
    public int makeCalculateStmt(Token calculateToken, ExpInfo expInfo1, ExpInfo expInfo2) {
        TokenType calTokenType = calculateToken.getType();
        if (expInfo1.varType != expInfo2.varType) {
            if (expInfo1.varType != VarType.Int && expInfo1.getValue() == null) {
                expInfo1 = makeTransStmt(expInfo1);
            }
            if (expInfo2.varType != VarType.Int && expInfo2.getValue() == null) {
                expInfo2 = makeTransStmt(expInfo2);
            }
        }
        VarType varType = expInfo1.getValue() == null ? expInfo2.varType : expInfo1.varType;
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calTokenType) + " " + varType2LengthMap.get(varType) + " " + expInfo1.getCalculateParam() + ", " + expInfo2.getCalculateParam();
        printStrings.add(printString);
        return dstReg;
    }

    public int makeLogicCalculateStmt(Token calculateToken, ExpInfo expInfo1, ExpInfo expInfo2) {
        if (expInfo1.varType != expInfo2.varType) {
            if (expInfo1.varType != VarType.Int && expInfo1.getValue() == null) {
                expInfo1 = makeTransStmt(expInfo1);
            }
            if (expInfo2.varType != VarType.Int && expInfo2.getValue() == null) {
                expInfo2 = makeTransStmt(expInfo2);
            }
        }
        VarType varType = expInfo1.getValue() == null ? expInfo2.varType : expInfo1.varType;
        int tmpReg = regIndex;
        String printString = getSpace() + getReg() + " = " + tokenType2CalculateTypeMap.get(calculateToken.getType()) + " " + varType2LengthMap.get(varType) +  " " + expInfo1.getCalculateParam() + ", " + expInfo2.getCalculateParam();
        printStrings.add(printString);
        int dstReg = regIndex;
        printString = getSpace() + getReg() + " = zext i1 %" + tmpReg + " to i32";
        printStrings.add(printString);
        return dstReg;
    }

    /**@return 分配寄存器编号 */
    private int makeAllocaStmt(VarType varType) {
        int srcRegPtr = regIndex;
        String printString = getSpace() + getReg() + " = alloca " + varType2LengthMap.get(varType);
        printStrings.add(printString);
        return srcRegPtr;
    }

    private int makeAllocaStmt(VarType varType, boolean isArray) {
        int srcRegPtr = regIndex;
        String printString = getSpace() + getReg() + " = alloca " + varType2LengthMap.get(varType) + (isArray ? "*" : "");
        printStrings.add(printString);
        return srcRegPtr;
    }

    private int makeAllocaStmt(VarType varType, int length) {
        int srcRegPtr = regIndex;
        String printString = getSpace() + getReg() + " = alloca [" + length + " x " + varType2LengthMap.get(varType) + "]";
        printStrings.add(printString);
        return srcRegPtr;
    }

    private void makeStoreImmStmt(int imm, int dstReg, VarType varType) {
        String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + imm + ", " + varType2LengthMap.get(varType) + "* " + index2Reg(dstReg);
        printStrings.add(printString);
    }

    // private void makeStoreRegStmt(int srcReg, ExpInfo dstExpInfo) {
    //     VarType varType = dstExpInfo.varType;
    //     boolean isGlobal = dstExpInfo.globalVarName != null;
    //     String printString = getSpace() + "store " + varType2LengthMap.get(varType) + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + "* " + getRegSymbol(isGlobal) + dstExpInfo.getReg();
    //     printStrings.add(printString);
    // }

    // private void makeStoreRegStmt(int srcReg, ExpInfo dstExpInfo, boolean isArray) {
    //     VarType varType = dstExpInfo.varType;
    //     boolean isGlobal = dstExpInfo.globalVarName != null;
    //     String printString = getSpace() + "store " + varType2LengthMap.get(varType) + (isArray ? "*" : "") + " " + index2Reg(srcReg) + ", " + varType2LengthMap.get(varType) + (isArray ? "*" : "") + "* " + getRegSymbol(isGlobal) + dstExpInfo.getReg();
    //     printStrings.add(printString);
    // }

    public void makeStoreStmt(ExpInfo srcExpInfo, ExpInfo dstExpInfo) {
        makeStoreStmt(srcExpInfo, dstExpInfo, dstExpInfo.isArray);
    }

    public void makeStoreStmt(ExpInfo srcExpInfo, ExpInfo dstExpInfo, boolean isArray) {
        VarType varType = dstExpInfo.varType;
        if (srcExpInfo.varType != varType && srcExpInfo.getValue() == null) {
            srcExpInfo = makeTransStmt(srcExpInfo);
        }
        String printString;
        printString = getSpace() + String.format("store %s %s, %s* %s", varType2LengthMap.get(varType), srcExpInfo.getCalculateParam(), varType2LengthMap.get(varType), dstExpInfo.getCalculateParam());
        printStrings.add(printString);
    }

    public int makeLoadStmt(ExpInfo expInfo) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = load " + varType2LengthMap.get(expInfo.varType) + ", " + varType2LengthMap.get(expInfo.varType) + "* " + (expInfo.isGlobal ? "@" : "%") + expInfo.getReg();
        printStrings.add(printString);
        return dstReg;
    }

    public int makeLoadStmt(ExpInfo expInfo, ExpInfo indexExpInfo) {
        int tmpreg = regIndex;
        String printString;
        if (expInfo.isGlobal == false) {
            printString = getSpace() + getReg() + " = getelementptr inbounds " + varType2LengthMap.get(expInfo.varType) + ", " + varType2LengthMap.get(expInfo.varType) + "* " + expInfo.getCalculateParam() + ", i32 " + indexExpInfo.getCalculateParam();
        } else {
            printString = getSpace() + getReg() + " = getelementptr inbounds [" + expInfo.length + " x " + varType2LengthMap.get(expInfo.varType) + "], [" + expInfo.length + " x " + varType2LengthMap.get(expInfo.varType) + "]* " + expInfo.getCalculateParam() + ", i32 0, i32 " + indexExpInfo.getCalculateParam();
        }
        printStrings.add(printString);
        int dstReg = makeLoadStmt(new ExpInfo(expInfo.varType, tmpreg));
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

    public boolean isDeclVarGlobal() {
        return isDeclVarGlobal;
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

    public int makeDeclStmt(ExpInfo expInfo) {
        return makeDeclStmt(expInfo, false);
    }

    public int makeDeclStmt(ExpInfo expInfo, boolean isArray) {
        int dstRegPtr = makeAllocaStmt(declareVarType, isArray);
        makeStoreStmt(expInfo, new ExpInfo(declareVarType, dstRegPtr), isArray);
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
            printString += varType2LengthMap.get(funcRParam.getVarType()) + (funcRParam.isArray() ? "*" : "") + " " + funcRParam.getExpInfo().getCalculateParam();
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

    public void makeConstrStmt(String constr, int num) {
        //不需要缩进
        int length = countConstrLength(constr) + 1;
        makeConstrStmt(constr, num, length);
    }
    
    public void makeConstrStmt(String constr, int num, int length) {
        String printConstr = "@.str." +  num + " = private unnamed_addr constant [" + length + " x i8] c\"" + dealConstr(constr, length) + "\"";
        strNum2LengthMap.put(num, length);
        printConstrs.add(printConstr);
    }

    public int makeStrDeclStmt(String str, String name) {
        int length = countConstrLength(str) + 1;
        return makeStrDeclStmt(str, name, length);
    }
    
    public int makeStrDeclStmt(String str, String name, int length) {
        str = str.substring(1, str.length() - 1);
        if (isDeclVarGlobal == true) {
            String printString = "@" + name + " = dso_local global [" + length + " x i8] c\"" + dealConstr(str, length) + "\"";
            printStrings.add(printString);
            return 0;
        } else {
            List<ExpInfo> expInfos = new ArrayList<>();
            int state = 0;
            for (Character c : str.toCharArray()) {
                switch (state) {
                    case 0:
                        if (c != '\\') {
                            expInfos.add(new ExpInfo(c, VarType.Char));
                        } else {
                            state = 1;
                        }
                        break;
                    case 1:
                        expInfos.add(new ExpInfo(tranCharacter(c), VarType.Char));
                        state = 0;
                    default:
                        break;
                }
            }
            return makeArrayDeclStmt(name, length, expInfos);
        }

    }

    public int makeArrayDeclStmt(String name, int length, List<ExpInfo> expInfos) {
        if (isDeclVarGlobal == true) {
            if (declareVarType.equals(VarType.Char)) {
                String str = "";
                for (ExpInfo expInfo : expInfos) {
                    str +=  tranCharacter(expInfo.getValue());
                }
                str = "\"" + str + "\"";
                return makeStrDeclStmt(str, name, length);
            }
            String printString = "@" + name + " = dso_local global [" + length + " x " + varType2LengthMap.get(declareVarType) + "]";
            if (expInfos == null || expInfos.isEmpty()) {
                printString += " zeroinitializer";
            } else {
                printString += " [";
                for (int i = 0; i < length; i++) {
                    if (i < expInfos.size()) {
                        printString += "i32 " + expInfos.get(i).getCalculateParam();
                    } else {
                        printString += "i32 0";
                    }
                    if (i != length - 1) {
                        printString += ", ";
                    }
                }
                printString += "]";
            }
            printStrings.add(printString);
            return 0;
        } else {
            int srcreg = makeAllocaStmt(declareVarType, length);
            int tmpreg = 0;
            for (int i = 0; i < length; i++) {
                String printString = "";
                if (i == 0) {
                    printString = getSpace() + getReg() + " = getelementptr inbounds [" + length + " x " + varType2LengthMap.get(declareVarType) + "], [" + length + " x " + varType2LengthMap.get(declareVarType) + "]* %" + srcreg + ", i32 0, i32 0";
                } else {
                    printString = getSpace() + getReg() + " = getelementptr inbounds " + varType2LengthMap.get(declareVarType) + ", " + varType2LengthMap.get(declareVarType) + "* " + " %" + tmpreg + ", i32 1";
                }
                printStrings.add(printString);
                tmpreg = regIndex - 1;
                if (i < expInfos.size()) {
                    makeStoreStmt(expInfos.get(i), new ExpInfo(declareVarType, tmpreg));
                } else {
                    makeStoreImmStmt(0, tmpreg, declareVarType);
                }
            }
            return srcreg + 1;
        }
    }

    public int makeGetelementptrStmt(ExpInfo expInfo) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = getelementptr inbounds [" + expInfo.length +" x " + varType2LengthMap.get(expInfo.varType) + "], [" + expInfo.length + " x " + varType2LengthMap.get(expInfo.varType) + "]* @" + expInfo.globalVarName + ", i32 0, i32 0";
        printStrings.add(printString);
        return dstReg;
    }

    public int makeGetelementptrStmt(ExpInfo expInfo, ExpInfo indexExpInfo) {
        int dstReg = regIndex;
        String printString;
        if (expInfo.isGlobal) {
            printString = getSpace() + getReg() + " = getelementptr inbounds [" + expInfo.length +" x " + varType2LengthMap.get(expInfo.varType) + "], [" + expInfo.length + " x " + varType2LengthMap.get(expInfo.varType) + "]* @" + expInfo.globalVarName + ", i32 0, i32 " + indexExpInfo.getCalculateParam();
        } else {
            printString = getSpace() + String.format("%s = getelementptr inbounds %s, %s* %s, i32 %s", getReg(), varType2LengthMap.get(expInfo.varType), varType2LengthMap.get(expInfo.varType), expInfo.getCalculateParam(), indexExpInfo.getCalculateParam());
        }
        printStrings.add(printString);
        return dstReg;
    }

    public void makeIfStmt(ExpInfo expInfo) {
        int dstReg = regIndex;
        String printString = getSpace() + getReg() + " = icmp ne " + varType2LengthMap.get(expInfo.varType) + " " + expInfo.getCalculateParam() + ", 0";
        printStrings.add(printString);
        printString = "if br in" + dstReg;
        printStrings.add(printString);
    }

    public void makeBrStmt() {
        String printString = String.format(getSpace() + "br label %%%d", regIndex);
        printStrings.add(printString);
    }

    public void makeBrStmt(String text) {
        String printString = "br " + text;
        printStrings.add(printString);
    }

    List<Integer> currentIfLabelsList = new ArrayList<>();
    Deque<List<Integer>> ifLabelsListsStack = new ArrayDeque<>();

    public void newIfLabelsList() {
        currentIfLabelsList = new ArrayList<>();
        ifLabelsListsStack.push(currentIfLabelsList);
    }

    public int setLabel() {
        return setLabel("");
    }

    public int setLabel(String text) {
        int label = regIndex;
        String printString = regIndex + ":  ; " + text;
        getReg();
        printStrings.add(printString);
        if (text.equals("and")) {
            currentIfLabelsList.add(label);
        }
        return label;
    }

    public void makeBrOutStmt() {
        String printString = "if br out";
        printStrings.add(printString);
    }

    public void setBrIn() {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "if br in";
            if (string.startsWith(tag)) {
                int reg = Integer.valueOf(string.substring(tag.length()));
                String tagAnd = "; and", tagOr = "; or", tagNode1 = "; node1", tagNode2 = "; node2", tagExit = "; exit";
                int labelAnd = -1, labelOr = -1, labelNode1 = -1, labelNode2 = -1, labelExit = -1;
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagAnd)) {
                        int index = string2.indexOf(':');
                        labelAnd = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                    if (string2.endsWith(tagNode1) && string2.endsWith(tagOr)) {
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagOr)) {
                        int index = string2.indexOf(':');
                        labelOr = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                    if (string2.endsWith(tagNode1)) {
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagNode1)) {
                        int index = string2.indexOf(':');
                        labelNode1 = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagNode2)) {
                        int index = string2.indexOf(':');
                        labelNode2 = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagExit)) {
                        int index = string2.indexOf(':');
                        labelExit = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                if (labelAnd != -1) {
                    if (labelOr != -1) {
                        printStrings.set(i, getSpace() + "br i1 %" + reg + ", label %" + labelAnd + ", label %" + labelOr);
                    }
                    else if (labelNode2 != -1) {
                        printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelAnd, labelNode2));
                    } else {
                        printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelAnd, labelExit));
                    }
                } else if (labelOr != -1) {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelOr));
                } else if (labelNode2 != -1) {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelNode2));
                } else {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelExit));
                }
            }
        }
    }

    public void setBrInFor() {
        int labelNode1 = -1;
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "if br in";
            if (string.startsWith(tag)) {
                int reg = Integer.valueOf(string.substring(tag.length()));
                String tagAnd = "; and", tagOr = "; or", tagNode1 = "; node1", tagNode2 = "; node2", tagExit = "; exit";
                int labelAnd = -1, labelOr = -1, /*labelNode1 = -1,*/ labelNode2 = -1, labelExit = -1;
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagAnd)) {
                        int index = string2.indexOf(':');
                        labelAnd = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                    if (string2.endsWith(tagNode1) && string2.endsWith(tagOr)) {
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagOr)) {
                        int index = string2.indexOf(':');
                        labelOr = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                    if (string2.endsWith(tagNode1)) {
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagNode1)) {
                        int index = string2.indexOf(':');
                        labelNode1 = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagNode2)) {
                        int index = string2.indexOf(':');
                        labelNode2 = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                for(int j = i; j < printStrings.size(); j++) {
                    String string2 = printStrings.get(j);
                    if (string2.endsWith(tagExit)) {
                        int index = string2.indexOf(':');
                        labelExit = Integer.valueOf(string2.substring(0, index));
                        break;
                    }
                }
                if (labelAnd != -1) {
                    if (labelOr != -1) {
                        printStrings.set(i, getSpace() + "br i1 %" + reg + ", label %" + labelAnd + ", label %" + labelOr);
                    }
                    else if (labelNode2 != -1) {
                        printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelAnd, labelNode2));
                    } else {
                        printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelAnd, labelExit));
                    }
                } else if (labelOr != -1) {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelOr));
                } else if (labelNode2 != -1) {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelNode2));
                } else {
                    printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode1, labelExit));
                }
            }
        }
    }

    public void setBrIn(int labelNode, int exitNode) {
        int index = 0;
        for (int i = 0; i < printStrings.size() && index < 2; i++) {
            String string = printStrings.get(i);
            String tag = "if br in";
            if (string.startsWith(tag)) {
                int reg = Integer.valueOf(string.substring(tag.length()));
                printStrings.set(i, getSpace() + String.format("br i1 %%%d, label %%%d, label %%%d", reg, labelNode, exitNode));
                index++;
            }
        }
    }

    public void setBr(String text, int label) {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            if (string.startsWith(text)) {
                printStrings.set(i, getSpace() + String.format("br label %%%d", label));
            }
        }
    }

    public void setBrOut(int label) {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "if br out";
            if (string.startsWith(tag)) {
                printStrings.set(i, getSpace() + "br label %" + label);
            }
        }
    }

    public void lockIF() {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "if br in";
            if (string.contains(tag)) {
                printStrings.set(i, "lockif" + string);
            }
        }
    }

    public void lockFor(String... strings) {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            for (String tag : strings) {
                if (string.contains(tag)) {
                    printStrings.set(i, "lockfor" + string);
                }
            }
        }
    }

    public void unlockIf() {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "lockif";
            if (string.startsWith(tag)) {
                printStrings.set(i, string.substring(tag.length()));
            }
        }
    }

    public void unlockFor() {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            String tag = "lockfor";
            if (string.startsWith(tag)) {
                printStrings.set(i, string.substring(tag.length()));
            }
        }
    }

    public void lockLabel(int label) {
        for (int i = 0; i < printStrings.size(); i++) {
            String string = printStrings.get(i);
            if (string.startsWith(label + ": ") && string.endsWith("lock") == false) {
                printStrings.set(i, string + ";lock");
                break;
            }
        }
    }

    public void lockIfLabel() {
        currentIfLabelsList = ifLabelsListsStack.pop();
        for (Integer integer : currentIfLabelsList) {
            lockLabel(integer);
        }
    }

    public void setAnd2Or() {
        for (int i = printStrings.size() - 1; i >= 0; i--) {
            String string = printStrings.get(i);
            String tag = "; and";
            if (string.endsWith(tag)) {
                int index = string.indexOf(';');
                printStrings.set(i, string.substring(0, index - 1) + "; or");
                break;
            }
        }
    }

    public void setOr2Null() {
        for (int i = printStrings.size() - 1; i >= 0; i--) {
            String string = printStrings.get(i);
            String tag = "; or";
            if (string.endsWith(tag)) {
                printStrings.remove(i);
                break;
            }
        }
    }

    public void removeLastOr() {
        for (int i = printStrings.size() - 1; i >= 0; i--) {
            String string = printStrings.get(i);
            String tag = "; or";
            if (string.endsWith(tag)) {
                printStrings.set(i, string.substring(0, string.length() - tag.length()));
                break;
            }
        }
        String printString = getSpace() + String.format("br label %%%d", regIndex);
        printStrings.add(printString);
    }

    private String dealConstr(String constr, int length) {
        constr = constr.replace("\\0", "\\00");
        constr = constr.replace("\\a", "\\07");
        constr = constr.replace("\\b", "\\08");
        constr = constr.replace("\\t", "\\09");
        constr = constr.replace("\\n", "\\0A");
        constr = constr.replace("\\v", "\\0B");
        constr = constr.replace("\\f", "\\0C");
        constr = constr.replace("\\\"", "\\22");
        constr = constr.replace("\\\'", "\\27");
        constr = constr.replace("\\\\", "\\5c");
        constr += "\\00".repeat(length - countConstrLengthForDealed(constr));
        return constr;
    }
    
    public int tranCharacter(char c) {
        switch (c) {
            case '0':
                return 0;
            case 'a':
                return 7;
            case 'b':
                return 8;
            case 't':
                return 9;
            case 'n':
                return 0xA;
            case 'v':
                return 0xB;
            case 'f':
                return 0xC;
            case '\"':
                return 0x22;
            case '\'':
                return 0x27;
            case '\\':
                return 0x5c;
            default:
                return c;
        }
    }

    public String tranCharacter(int c) {
        switch (c) {
            case '\0':
                return "\\0";
            case 7:
                return "\\a";
            case '\b':
                return "\\b";
            case '\t':
                return "\\t";
            case '\n':
                return "\\n";
            case 11:
                return "\\v";
            case '\f':
                return "\\f";
            case '\"':
                return "\\\"";
            case '\'':
                return "\\\'";
            case '\\':
                return "\\\\";
            default:
                return ((char) c ) + "";
        }
    }

    private static int countConstrLengthForDealed(String constr) {
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
                    state = 2;
                    break;
                case 2:
                    state = 0;
                    break;
                default:
                    break;
            }
        }
        return length;
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

}
