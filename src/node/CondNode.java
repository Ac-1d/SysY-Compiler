package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;

public class CondNode {//finish
    // Cond → LOrExp 

    LOrExpNode lOrExpNode;
    ExpInfo expInfo = new ExpInfo();

    public static CondNode Cond() {
        CondNode condNode = new CondNode();
        LOrExpNode lOrExpNode;
        lOrExpNode = LOrExpNode.LOrExp();
        if(lOrExpNode == null) {
            return null;
        }
        condNode.lOrExpNode = lOrExpNode;
        return condNode;
    }

    void print() {
        lOrExpNode.print();
        System.out.println(toString());
    }

    void makeLLVM() {
        try {
            expInfo.setValue(lOrExpNode.calculateConstExp());
        } catch (ExpNotConstException e) {
            lOrExpNode.makeLLVM();
            expInfo = lOrExpNode.expInfo;
        }
    }

    public String toString() {
        return "<Cond>";
    }

    private CondNode() {}
}
