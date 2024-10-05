package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class FuncTypeNode {
    // FuncType → 'void' | 'int' | 'char'

    Token funcTypeToken;

    public static FuncTypeNode FuncType() {
        Parse instance = Parse.getInstance();
        FuncTypeNode funcTypeNode = new FuncTypeNode();
        Token token;
        token = instance.peekNextToken();
        TokenType tokenType = token.getType();
        if(tokenType.equals(TokenType.VOIDTK) == false && tokenType.equals(TokenType.INTTK) == false && tokenType.equals(TokenType.CHARTK) == false) {
            
        }
        return funcTypeNode;
    }
}
