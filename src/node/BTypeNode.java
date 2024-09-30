package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class BTypeNode {
    //BType → 'int' | 'char'
    Token intOrCharToken;
    
    public static BTypeNode BType() {//finish
        Parse instance = Parse.getInstance();
        BTypeNode bTypeNode = new BTypeNode();
        Token token;
        token = instance.peekNextToken();
        bTypeNode.intOrCharToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.INTTK) == false && token.getType().equals(TokenType.CHARTK)) {
            return null;
        }
        return bTypeNode;
    }

    public void print() {
        System.out.println(intOrCharToken.toString());
    }
    
}
