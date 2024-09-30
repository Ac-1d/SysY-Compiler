package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class ConstDefNode {
    //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    Token identToken;
    DefArrayNode defArrayNode;
    Token eqlToken;
    ConstInitValNode constInitValNode;
    
    public static ConstDefNode ConstDef() {
        Parse instance = Parse.getInstance();
        ConstDefNode constDefNode = new ConstDefNode();
        constDefNode.identToken = instance.peekNextToken();//TODO: change
        if(instance.peekNextToken().getType().equals(TokenType.LBRACK)) {// 下一个符号是'['
            constDefNode.defArrayNode = DefArrayNode.DefArray();
        }
        // 下一个符号是'='
        constDefNode.constInitValNode = ConstInitValNode.ConstInitVal();
        return constDefNode;
    }

    class DefArrayNode {
        Token LBRACK;
        ConstExpNode constExpNode;
        Token RBRACK;

        public static DefArrayNode DefArray() {
            ConstDefNode constDefNode = new ConstDefNode();
            DefArrayNode defArrayNode = constDefNode.new DefArrayNode();
            //第一个token一定是'['


            return defArrayNode;
        } 

        private boolean check() {
            return true;
        }

        private void next() {

        }
    }
}
