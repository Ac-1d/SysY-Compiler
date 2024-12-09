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
            expInfo.setValue(calculateConstExp());
        } catch (Exception e) {
            lOrExpNode.makeLLVM();
            
        }
    }

    int calculateConstExp() throws ExpNotConstException {
        return lOrExpNode.calculateConstExp() == true ? 1 : 0;
    }

    public String toString() {
        return "<Cond>";
    }

    private CondNode() {}
}
