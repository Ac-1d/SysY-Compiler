package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class MulExpNode {//finish
    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    // change it the same as AddExpNode

    UnaryExpNode unaryExpNode;
    Token mulToken;
    MulExpNode shorterMulExpNode;

    public static MulExpNode MulExp() {
        Parse instance = Parse.getInstance();
        MulExpNode mulExpNode = new MulExpNode();
        UnaryExpNode unaryExpNode;
        Token mulToken;
        MulExpNode shorterMulExpNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        unaryExpNode = UnaryExpNode.UnaryExp();
        if(unaryExpNode == null) {
            instance.setPeekIndex(tmpIndex);
            return null;
        }
        mulExpNode.unaryExpNode = unaryExpNode;
        tmpIndex = instance.getPeekIndex();
        mulToken = instance.peekNextToken();
        TokenType tmpTokenType = mulToken.getType();
        if(tmpTokenType.equals(TokenType.MULT) == false && tmpTokenType.equals(TokenType.DIV) == false && tmpTokenType.equals(TokenType.MOD) == false) {
            instance.setPeekIndex(tmpIndex);
            return mulExpNode;
        }
        mulExpNode.mulToken = mulToken;
        shorterMulExpNode = MulExpNode.MulExp();
        mulExpNode.shorterMulExpNode = shorterMulExpNode;
        return mulExpNode;
    }

    private MulExpNode() {}
}
