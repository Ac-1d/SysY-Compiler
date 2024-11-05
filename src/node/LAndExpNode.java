package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class LAndExpNode {//finish
    // LAndExp → EqExp | LAndExp '&&' EqExp

    EqExpNode eqExpNode;
    LAndExpNode shorterLAndExpNode;
    Token andToken;

    public static LAndExpNode LAndExp() {
        Parser instance = Parser.getInstance();
        LAndExpNode lAndExpNode = new LAndExpNode();
        EqExpNode eqExpNode;
        LAndExpNode shorterAndExpNode;
        Token andToken;
        int tmpIndex;
        eqExpNode = EqExpNode.EqExp();
        if(eqExpNode == null) {
            return null;
        }
        lAndExpNode.eqExpNode = eqExpNode;
        tmpIndex = instance.getPeekIndex();
        andToken = instance.peekNextToken();
        if(andToken.getType().equals(TokenType.AND) == false) {
            instance.setPeekIndex(tmpIndex);
            return lAndExpNode;
        }
        lAndExpNode.andToken = andToken;
        shorterAndExpNode = LAndExpNode.LAndExp();
        if(shorterAndExpNode == null) {
            return null;
        }
        lAndExpNode.shorterLAndExpNode = shorterAndExpNode;
        return lAndExpNode;
    }

    void print() {
        eqExpNode.print();
        System.out.println(toString());
        if(shorterLAndExpNode != null) {
            andToken.print();
            shorterLAndExpNode.print();
        }
    }

    void setupSymbolTable() {
        eqExpNode.setupSymbolTable();
        if (shorterLAndExpNode != null) {
            shorterLAndExpNode.setupSymbolTable();
        }
    }

    void makeLLVM() {
        
    }

    @Override
    public String toString() {
        return "<LAndExp>";
    }

    private LAndExpNode() {}
}
