package node;

import error.Error;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class MainFuncDefNode {//finish
    // MainFuncDef → 'int' 'main' '(' ')' Block

    Token intToken;
    Token mainToken;
    Token lparentToken;
    Token rparentToken;
    BlockNode blockNode;

    public static MainFuncDefNode MainFuncDef() {
        Parser instance = Parser.getInstance();
        MainFuncDefNode mainFuncDefNode = new MainFuncDefNode();
        Token token;
        BlockNode blockNode;
        int tmpIndex;
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.INTTK) == false) {
            return null;
        }
        mainFuncDefNode.intToken = token;
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.MAINTK) == false) {
            return null;
        }
        mainFuncDefNode.mainToken = token;
        if(token.getType().equals(TokenType.LPARENT) == false) {
            return null;
        }
        mainFuncDefNode.lparentToken = token;
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.RPARENT) == false) {//error
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(token), 'j'));
            mainFuncDefNode.rparentToken.setLineNum(instance.getPreTokenLineNum(token));
        }
        else {
            mainFuncDefNode.rparentToken = token;
        }
        blockNode = BlockNode.Block();
        if(blockNode == null) {
            return null;
        }
        mainFuncDefNode.blockNode = blockNode;
        return mainFuncDefNode;
    }

    void print() {
        intToken.print();
        mainToken.print();
        lparentToken.print();
        rparentToken.print();
        blockNode.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<MainFuncDefNode>";
    }

    private MainFuncDefNode() {}
    
}
