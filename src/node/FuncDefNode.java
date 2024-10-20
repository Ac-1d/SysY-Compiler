package node;

import Symbol.FuncParam;
import Symbol.FuncSymbol;
import Symbol.FuncType;
import Symbol.SymbolTable;
import Symbol.VarSymbol;
import Symbol.VarType;
import error.Error;
import error.ErrorType;
import frontend.ErrorHandler;
import frontend.Parser;
import frontend.SymbolHandler;
import token.Token;
import token.TokenType;

public class FuncDefNode {//finish
    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    // FuncType → 'void' | 'int' | 'char'
    // FuncFParams → FuncFParam { ',' FuncFParam }
    // FuncFParam → BType Ident ['[' ']']

    FuncTypeNode funcTypeNode;
    Token identToken;
    Token lparentToken;
    FuncFParamsNode funcFParamsNode;
    Token rparentToken;
    BlockNode blockNode;
    FuncSymbol funcSymbol;

    public static FuncDefNode FuncDef() {
        Parser instance = Parser.getInstance();
        FuncDefNode funcDefNode = new FuncDefNode();
        FuncTypeNode funcTypeNode;
        Token indentToken;
        Token lparentToken;
        FuncFParamsNode funcFParamsNode;
        Token rparentToken;
        BlockNode blockNode;
        int tmpIndex;
        funcTypeNode = FuncTypeNode.FuncType();
        if(funcTypeNode == null) {
            return null;
        }
        funcDefNode.funcTypeNode = funcTypeNode;
        indentToken = instance.peekNextToken();
        if(indentToken.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        funcDefNode.identToken = indentToken;
        lparentToken = instance.peekNextToken();
        if(lparentToken.getType().equals(TokenType.LPARENT) == false) {
            return null;
        }
        funcDefNode.lparentToken = lparentToken;
        tmpIndex = instance.getPeekIndex();
        funcFParamsNode = FuncFParamsNode.FuncFParams();
        if(funcFParamsNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        else {
            funcDefNode.funcFParamsNode = funcFParamsNode;
        }
        tmpIndex = instance.getPeekIndex();
        rparentToken = instance.peekNextToken();
        if(rparentToken.getType().equals(TokenType.RPARENT) == false) {//error
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error(instance.getPreTokenLineNum(rparentToken), ErrorType.j));
            funcDefNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
        }
        else {
            funcDefNode.rparentToken = rparentToken;
        }
        blockNode = BlockNode.Block();
        if(blockNode == null) {
            return null;
        }
        funcDefNode.blockNode = blockNode;
        return funcDefNode;
    }

    void print() {
        funcTypeNode.print();
        identToken.print();
        lparentToken.print();
        if(funcFParamsNode != null) {
            funcFParamsNode.print();
        }
        rparentToken.print();
        blockNode.print();
        System.out.println(toString());
    }

    void setupSymbolTable() {
        SymbolHandler instance = SymbolHandler.getInstance();
        FuncType funcType = SymbolHandler.getFuncType(funcTypeNode.funcTypeToken);
        this.funcSymbol = new FuncSymbol(identToken, funcType);
        instance.addSymbol(funcSymbol);
        instance.setCurSymbolTable(new SymbolTable(instance.getCurSymbolTable()));
        if(funcFParamsNode != null) {//有参数
            FuncFParamNode funcFParamNode = funcFParamsNode.funcFParamNode;
            Token identToken = funcFParamNode.identToken;
            VarType varType = SymbolHandler.getVarType(funcFParamNode.bTypeNode.intOrCharToken);
            boolean isArray = funcFParamNode.lbrackToken != null;
            boolean isConst = false;
            funcSymbol.addFuncParam(new FuncParam(varType, isArray));
            instance.addSymbol(new VarSymbol(identToken, varType, isConst, isArray));
            for (FuncFParamsNode.FuncFParamWithCommaNode funcFParamWithCommaNode : funcFParamsNode.funcFParamWithCommaNodesList) {
                funcFParamNode = funcFParamWithCommaNode.funcFParamNode;
                identToken = funcFParamNode.identToken;
                varType = SymbolHandler.getVarType(funcFParamNode.bTypeNode.intOrCharToken);
                isArray = funcFParamNode.lbrackToken != null;
                funcSymbol.addFuncParam(new FuncParam(varType, isArray));
                instance.addSymbol(new VarSymbol(identToken, varType, isConst, isArray));
            }
        }
        blockNode.setupSymbolTable(true);
        instance.setCurSymbolTable(instance.getCurSymbolTable().getFatherSymbolTable());
        if (funcSymbol.getFuncType().equals(FuncType.Void)) {// check error.f
            for (BlockItemNode blockItemNode : blockNode.blockItemNodesList) {
                blockItemNode.checkVoidFuncReturn();
            }
        } else {// check error.g
            blockNode.checkFuncHasReturn();
        }
    }


    @Override
    public String toString() {
        return "<FuncDef>";
    }

    private FuncDefNode() {
        rparentToken = new Token(TokenType.RPARENT, ")");
        
    }
    
}
