package node;

public class CondNode {//finish
    // Cond → LOrExp 

    LOrExpNode lOrExpNode;

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

    public String toString() {
        return "<CondNode>";
    }

    private CondNode() {}
}
