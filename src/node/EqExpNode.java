package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class EqExpNode {//finish
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    // change it the same as AddExp

    RelExpNode relExpNode;
    EqExpNode shorterEqExpNode;
    Token eqlOrNeqToken;

    public static EqExpNode EqExp() {
        Parser instance = Parser.getInstance();
        EqExpNode eqExpNode = new EqExpNode();
        RelExpNode relExpNode;
        EqExpNode shorterEqExpNode;
        Token eqlOrNeqToken;
        int tmpIndex;
        relExpNode = RelExpNode.RelExp();
        if(relExpNode == null) {
            return null;
        }
        eqExpNode.relExpNode = relExpNode;
        tmpIndex = instance.getPeekIndex();
        eqlOrNeqToken = instance.peekNextToken();
        if(eqlOrNeqToken.getType().equals(TokenType.EQL) == false && eqlOrNeqToken.getType().equals(TokenType.NEQ) == false) {
            instance.setPeekIndex(tmpIndex);
            return eqExpNode;
        }
        eqExpNode.eqlOrNeqToken = eqlOrNeqToken;
        tmpIndex = instance.getPeekIndex();
        shorterEqExpNode = EqExpNode.EqExp();
        if(shorterEqExpNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        else {
            eqExpNode.shorterEqExpNode = shorterEqExpNode;
        }
        return eqExpNode;
    }

    void print(boolean state) {
        relExpNode.print(true);
        if(state == true) {
            System.out.println(toString());
        }
        if(shorterEqExpNode != null) {
            eqlOrNeqToken.print();
            shorterEqExpNode.print(false);
            if(state == true) {
                System.out.println(toString());
            }
        }
    }

    @Override
    public String toString() {
        return "<EqExp>";
    }

    private EqExpNode() {}
}
