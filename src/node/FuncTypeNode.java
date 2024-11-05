package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class FuncTypeNode {//finish
    // FuncType → 'void' | 'int' | 'char'

    Token funcTypeToken;

    public static FuncTypeNode FuncType() {
        Parser instance = Parser.getInstance();
        FuncTypeNode funcTypeNode = new FuncTypeNode();
        Token token;
        token = instance.peekNextToken();
        TokenType tokenType = token.getType();
        if(tokenType.equals(TokenType.VOIDTK) == false && tokenType.equals(TokenType.INTTK) == false && tokenType.equals(TokenType.CHARTK) == false) {
            return null;
        }
        funcTypeNode.funcTypeToken = token;
        return funcTypeNode;
    }

    void print() {
        funcTypeToken.print();
        System.out.println(toString());
    }

    void makeLLVM() {
        
    }

    @Override
    public String toString() {
        return "<FuncType>";
    }

    private FuncTypeNode() {}
}
