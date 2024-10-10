package node;

import error.Error;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class FuncDefNode {//finish
    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block

    FuncTypeNode funcTypeNode;
    Token identToken;
    Token lparentToken;
    FuncFParamsNode funcFParamsNode;
    Token rparentToken;
    BlockNode blockNode;

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
            instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(rparentToken), 'j'));
        }
        funcDefNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
        blockNode = BlockNode.Block();
        if(blockNode == null) {
            return null;
        }
        funcDefNode.blockNode = blockNode;
        return funcDefNode;
    }

    private FuncDefNode() {
        rparentToken = new Token(TokenType.RPARENT, ")");
        
    }
    
}
