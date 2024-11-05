package node;

import Symbol.FuncParam;
import Symbol.FuncSymbol;
import Symbol.Symbol;
import Symbol.SymbolTable;
import error.Error;
import error.ErrorType;
import frontend.ErrorHandler;
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

    void setupSymbolTable() {
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        ErrorHandler errorHandler = ErrorHandler.getInstance();
        SymbolTable symbolTable;
        switch (state) {
            case 1:
                primaryExpNode.setupSymbolTable();
                break;
            case 2:
                symbolTable = symbolHandler.findSymbolTableHasIdent(identToken);
                if(symbolTable == null) {
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
                    funcRParamsNode.setupSymbolTable();
                }
                checkRParamNumError();
                checkRParamTypeError();
                break;
            case 3:
                shortreUnaryExpNode.setupSymbolTable();
            default:
                break;
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
                if (funcParam.getIsArray() != funcRParamsNode.expNode.isArray) {//数组与变量
                    errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                    break;
                }
                if (funcParam.getIsArray() == true) {
                    if (funcParam.getVarType().equals(funcRParamsNode.expNode.varType) == false) {//数组，但是类型不同
                        errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                        break;
                    }
                }
            } else {
                expNode = funcRParamsNode.paramNodesList.get(i - 1).expNode;
                if (funcParam.getIsArray() != expNode.isArray) {//数组与变量
                    errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                    break;
                }
                if (funcParam.getIsArray() == true) {
                    if (funcParam.getVarType().equals(expNode.varType) == false) {//数组，但是类型不同
                        errorHandler.addError(new Error(identToken.getLineNum(), ErrorType.e));
                        break;
                    }
                }
            }
        }
    }

    void makeLLVM() {
        
    }

    @Override
    public String toString() {
        return "<UnaryExp>";
    }

    private UnaryExpNode() {
        rparentToken = new Token(TokenType.RPARENT, ")");
    }
}
