package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import Symbol.FuncParam;
import Symbol.FuncRParam;
import Symbol.FuncSymbol;
import Symbol.FuncType;
import Symbol.Symbol;
import Symbol.SymbolTable;
import Symbol.VarType;
import error.Error;
import error.ErrorType;
import frontend.ErrorHandler;
import frontend.LLVMGenerator;
import frontend.Parser;
import frontend.SymbolHandler;
import token.Token;
import token.TokenType;

public class UnaryExpNode {//finish
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    // FuncRParams → Exp { ',' Exp } 
    // WTF EXP IS TERRIBLE!

    PrimaryExpNode primaryExpNode;
    Token identToken;
    Token lparentToken;
    FuncRParamsNode funcRParamsNode;
    Token rparentToken;
    UnaryOpNode unaryOpNode;
    UnaryExpNode shortreUnaryExpNode;
    FuncSymbol funcSymbol;
    int state;
    ExpInfo expInfo = new ExpInfo();

    public static UnaryExpNode UnaryExp() {
        Parser instance = Parser.getInstance();
        UnaryExpNode unaryExpNode = new UnaryExpNode();
        PrimaryExpNode primaryExpNode;
        Token identToken;
        Token lparentToken;
        FuncRParamsNode funcRParamsNode;
        Token rparentToken;
        UnaryOpNode unaryOpNode;
        UnaryExpNode shorterUnaryExpNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        identToken = instance.peekNextToken();
        if(identToken.getType().equals(TokenType.IDENFR)) {//case 2
            do {
                unaryExpNode.identToken = identToken;
                lparentToken = instance.peekNextToken();
                if(lparentToken.getType().equals(TokenType.LPARENT) == false) {
                    break;
                }
                unaryExpNode.lparentToken = lparentToken;
                int ttmpIndex = instance.getPeekIndex();
                funcRParamsNode = FuncRParamsNode.FuncRParams();
                if(funcRParamsNode == null) {
                    instance.setPeekIndex(ttmpIndex);
                }
                unaryExpNode.funcRParamsNode = funcRParamsNode;
                ttmpIndex = instance.getPeekIndex();
                rparentToken = instance.peekNextToken();
                if(rparentToken.getType().equals(TokenType.RPARENT) == false) {
                    instance.errorsList.add(new Error(instance.getPreTokenLineNum(rparentToken), ErrorType.j));
                    instance.setPeekIndex(ttmpIndex);
                }
                unaryExpNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
                unaryExpNode.state = 2;
                return unaryExpNode;
            } while (true);
        }
        instance.setPeekIndex(tmpIndex);
        primaryExpNode = PrimaryExpNode.primaryExp();
        if(primaryExpNode != null) {//case 1
            unaryExpNode.primaryExpNode = primaryExpNode;
            unaryExpNode.state = 1;
            return unaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        unaryOpNode = UnaryOpNode.UnaryOp();
        if(unaryOpNode != null) {//case 3
            unaryExpNode.unaryOpNode = unaryOpNode;
            shorterUnaryExpNode = UnaryExpNode.UnaryExp();
            unaryExpNode.shortreUnaryExpNode = shorterUnaryExpNode;
            unaryExpNode.state = 3;
            return unaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    void print() {
        switch (state) {
            case 1:
                primaryExpNode.print();
                break;
            case 2:
                identToken.print();
                lparentToken.print();
                if(funcRParamsNode != null) {
                    funcRParamsNode.print();
                }
                rparentToken.print();
                break;
            case 3:
                unaryOpNode.print();
                shortreUnaryExpNode.print();
                break;
            default:
                break;
        }
        System.out.println(toString());
    }

    void makeLLVM() {
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        SymbolTable symbolTable;
        switch (state) {
            case 1:
                primaryExpNode.makeLLVM();
                expInfo = primaryExpNode.expInfo;
                break;
            case 2:
                symbolTable = symbolHandler.findSymbolTableHasIdent(identToken);
                if (symbolTable == null) {
                    errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.c));
                } else {
                    Symbol symbol = symbolTable.findSymbol(identToken);
                    boolean isFuncSymbol = symbol.getClass().equals(FuncSymbol.class);
                    if (isFuncSymbol == false) {
                        errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.c));
                    } else {
                        funcSymbol = (FuncSymbol) symbol;
                    }
                }
                if (funcRParamsNode != null) {
                    funcRParamsNode.llvm();
                    FuncRParam[] funcRParams = new FuncRParam[funcRParamsNode.expInfos.size()];
                    // List<FuncRParam> funcRParams = new ArrayList<>();
                    for (int i = 0; i < funcRParams.length; i++) {
                        ExpInfo expInfo = funcRParamsNode.expInfos.get(i);
                        FuncParam funcParam = funcSymbol.getFuncParamsList().get(i);
                        funcRParams[i] = new FuncRParam(expInfo, funcParam);
                    }
                    expInfo.setReg(llvmGenerator.makeCallFunctionStmt(identToken.getWord(), funcSymbol.getFuncType(), funcRParams));
                    if (funcSymbol.getFuncType().equals(FuncType.Int)) {
                        expInfo.varType = VarType.Int;
                    } else if (funcSymbol.getFuncType().equals(FuncType.Char)) {
                        expInfo.varType = VarType.Char;
                    }
                } else {
                    expInfo.setReg(llvmGenerator.makeCallFunctionStmt(identToken.getWord(), funcSymbol.getFuncType()));
                }
                expInfo.setVarType(funcSymbol.getFuncType());

                checkRParamNumError();
                checkRParamTypeError();
                break;
            case 3://此处未考虑unaryOp为!的情况
                shortreUnaryExpNode.makeLLVM();
                expInfo = shortreUnaryExpNode.expInfo;
                if (unaryOpNode.unaryOpToken.getType().equals(TokenType.NOT) == false) {
                    expInfo.setReg(llvmGenerator.makeCalculateStmt(unaryOpNode.unaryOpToken, new ExpInfo(0, expInfo.varType), expInfo));
                } else {
                    expInfo.setReg(llvmGenerator.makeLogicCalculateStmt(unaryOpNode.unaryOpToken, new ExpInfo(0, expInfo.varType), expInfo));
                    expInfo.varType = VarType.Int;
                }
            default:
                break;
        }
    }

    int calculateConstExp(boolean isConst) throws ExpNotConstException {
        switch (state) {
            case 1:
                return primaryExpNode.calculateConstExp(isConst);
            case 3:
                if (unaryOpNode.unaryOpToken.getType().equals(TokenType.PLUS) == true) {
                    return shortreUnaryExpNode.calculateConstExp(isConst);
                } else if (unaryOpNode.unaryOpToken.getType().equals(TokenType.MINU) == true) {
                    return - shortreUnaryExpNode.calculateConstExp(isConst);
                } else {
                    return shortreUnaryExpNode.calculateConstExp(isConst) == 0 ? 1 : 0;
                }
            default:
                throw new ExpNotConstException();
        }
    }

    void checkRParamNumError() {
        ErrorHandler instance = ErrorHandler.getInstance();
        if (funcSymbol == null) {
            return;
        }
        if (funcRParamsNode == null) {
            return;
        }
        int paramsNum = funcSymbol.getParamsNum();
        // 1 + n (Exp + List)
        if(paramsNum != (1 + funcRParamsNode.paramNodesList.size())) {
            instance.addError(new Error(identToken.getLineNum(), ErrorType.d));
        }
    }

    void checkRParamTypeError() {
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        if (funcRParamsNode == null) {
            return;
        }
        for (int i = 0; i < funcSymbol.getParamsNum() && i < 1 + funcRParamsNode.paramNodesList.size(); i++) {
            FuncParam funcParam = funcSymbol.getFuncParamsList().get(i);
            ExpNode expNode;
            if (i == 0) {//exp
                if (funcParam.isArray() != funcRParamsNode.expNode.isArray) {//数组与变量
                    errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                    break;
                }
                if (funcParam.isArray() == true) {
                    if (funcParam.getVarType().equals(funcRParamsNode.expNode.varType) == false) {//数组，但是类型不同
                        errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                        break;
                    }
                }
            } else {
                expNode = funcRParamsNode.paramNodesList.get(i - 1).expNode;
                if (funcParam.isArray() != expNode.isArray) {//数组与变量
                    errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                    break;
                }
                if (funcParam.isArray() == true) {
                    if (funcParam.getVarType().equals(expNode.varType) == false) {//数组，但是类型不同
                        errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "<UnaryExp>";
    }

    private UnaryExpNode() {
        rparentToken = new Token(TokenType.RPARENT, ")");
    }
}
