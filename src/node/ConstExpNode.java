package node;

public class expNode {//finish
    //ConstExp → AddExp
    AddExpNode addExpNode;

    public static expNode ConstExp() {
        expNode constExpNode = new expNode();
        AddExpNode addExpNode;
        addExpNode = AddExpNode.AddExp();
        if(addExpNode == null) {
            return null;
        }
        constExpNode.addExpNode = addExpNode;
        return constExpNode;
    }

    void print() {
        addExpNode.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<ConstExpNode>";
    }

    private expNode() {}
}
