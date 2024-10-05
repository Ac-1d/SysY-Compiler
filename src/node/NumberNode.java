package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class NumberNode {//finish
    // Number → IntConst

    Token intConstToken;

    public static NumberNode Number() {
        Parse instance = Parse.getInstance();
        NumberNode numberNode = new NumberNode();
        Token intConstToken;
        intConstToken = instance.peekNextToken();
        if(intConstToken.getType().equals(TokenType.INTCON) == false) {
            return null;
        }
        numberNode.intConstToken = intConstToken;
        return numberNode;
    }

    private NumberNode() {}
}
