package node;

import token.Token;
import token.TokenType;

public class ConstDeclNode {
    //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    Token constToken;
    BTypeNode bTypeNode;

    public static ConstDeclNode constDecl() {
        ConstDeclNode constDeclNode = new ConstDeclNode();
        
        return constDeclNode;
    }

    public ConstDeclNode() {
        constToken = new Token(TokenType.CONSTTK, "const");
    }
    
}
