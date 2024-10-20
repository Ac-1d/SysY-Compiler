package node;

import error.Error;
import error.ErrorType;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class FuncFParamNode {
    // FuncFParam → BType Ident ['[' ']']

    BTypeNode bTypeNode;
    Token identToken;
    Token lbrackToken;
    Token rbrackToken;

    public static FuncFParamNode FuncFParam() {
        Parser instance = Parser.getInstance();
        FuncFParamNode funcFParamNode = new FuncFParamNode();
        Token token;
        BTypeNode bTypeNode;
        int tmpIndex;
        bTypeNode = BTypeNode.BType();
        if(bTypeNode == null) {
            return null;
        }
        funcFParamNode.bTypeNode = bTypeNode;
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        funcFParamNode.identToken = token;
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.LBRACK) == false) {
            instance.setPeekIndex(tmpIndex);
            return funcFParamNode;
        }
        funcFParamNode.lbrackToken = token;
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.RBRACK) == false) {//error
            instance.setPeekIndex(tmpIndex);
            instance.errorsList.add(new Error(instance.getPreTokenLineNum(token), ErrorType.k));
            funcFParamNode.rbrackToken.setLineNum(instance.getPreTokenLineNum(token));
        }
        funcFParamNode.rbrackToken = token;

        return funcFParamNode;
    }

    void print() {
        bTypeNode.print();
        identToken.print();
        if(lbrackToken != null) {
            lbrackToken.print();
            rbrackToken.print();
        }
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<FuncFParam>";
    }

    private FuncFParamNode() {
        rbrackToken = new Token(TokenType.RBRACK, "]");
    }
}
