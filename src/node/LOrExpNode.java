package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class LOrExpNode {//finish
    // LOrExp → LAndExp | LOrExp '||' LAndExp
    
    LAndExpNode lAndExpNode;
    LOrExpNode shorterLOrExpNode;
    Token orToken;

    public static LOrExpNode LOrExp() {
        Parser instance = Parser.getInstance();
        LOrExpNode lOrExpNode = new LOrExpNode();
        LAndExpNode lAndExpNode;
        LOrExpNode shoterLOrExpNode;
        Token orToken;
        int tmpIndex;
        lAndExpNode = LAndExpNode.LAndExp();
        if(lAndExpNode == null) {
            return null;
        }
        lOrExpNode.lAndExpNode = lAndExpNode;
        tmpIndex = instance.getPeekIndex();
        orToken = instance.peekNextToken();
        if(orToken.getType().equals(TokenType.OR) == false) {
            instance.setPeekIndex(tmpIndex);
            return lOrExpNode;
        }
        lOrExpNode.orToken = orToken;
        shoterLOrExpNode = LOrExpNode.LOrExp();
        if(shoterLOrExpNode == null) {
            return null;
        }
        lOrExpNode.shorterLOrExpNode = shoterLOrExpNode;
        return lOrExpNode;
    }

    void print() {
        lAndExpNode.print();
        System.out.println(toString());
        if(shorterLOrExpNode != null) {
            orToken.print();
            shorterLOrExpNode.print();
        }
    }

    void setupSymbolTable() {
        lAndExpNode.setupSymbolTable();
        if (shorterLOrExpNode != null) {
            shorterLOrExpNode.setupSymbolTable();
        }
    }

    @Override
    public String toString() {
        return "<LOrExp>";
    }

    private LOrExpNode() {}
}
