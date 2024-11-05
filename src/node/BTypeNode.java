package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class BTypeNode {//finish
    // BType → 'int' | 'char'
    // dont print

    Token intOrCharToken;
    
    public static BTypeNode BType() {
        Parser instance = Parser.getInstance();
        BTypeNode bTypeNode = new BTypeNode();
        Token token;
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.INTTK) == false && token.getType().equals(TokenType.CHARTK) == false) {
            return null;
        }
        bTypeNode.intOrCharToken = token;
        return bTypeNode;
    }

    public void print() {
        intOrCharToken.print();
    }

    void makeLLVM() {
        
    }

    private BTypeNode() {}
    
}
