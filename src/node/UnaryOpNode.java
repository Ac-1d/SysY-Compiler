package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class UnaryOpNode {//finish
    // UnaryOp → '+' | '−' | '!'
    Token unaryOpToken;

    public static UnaryOpNode UnaryOp() {
        Parser instance = Parser.getInstance();
        UnaryOpNode unaryOpNode = new UnaryOpNode();
        Token unaryOpToken;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        unaryOpToken = instance.peekNextToken();
        TokenType tmpTokenType = unaryOpToken.getType();
        if(tmpTokenType.equals(TokenType.PLUS) == false && tmpTokenType.equals(TokenType.MINU) == false && tmpTokenType.equals(TokenType.NOT) == false) {
            instance.setPeekIndex(tmpIndex);
            return null;
        }
        unaryOpNode.unaryOpToken = unaryOpToken;
        return unaryOpNode;
    }

    void print() {
        unaryOpToken.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<UnaryOpNode>";
    }

    private UnaryOpNode() {}
}
