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

    void print() {
        addExpNode.print();
        System.out.println(toString());
    }

    void setupSymbolTable() {
        addExpNode.setupSymbolTable();
    }

    @Override
    public String toString() {
        return "<Exp>";
    }

    private ExpNode() {}
}
