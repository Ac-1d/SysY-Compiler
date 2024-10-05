package node;

public class ExpNode {//finish
    // Exp → AddExp

    AddExpNode addExpNode;

    public static ExpNode Exp() {
        ExpNode expNode = new ExpNode();
        AddExpNode addExpNode;
        addExpNode = AddExpNode.AddExp();
        if(addExpNode == null) {
            return null;
        }
        expNode.addExpNode = addExpNode;
        return expNode;
    }

    private ExpNode() {}
}
