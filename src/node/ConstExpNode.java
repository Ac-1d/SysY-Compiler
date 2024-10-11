package node;

public class ConstExpNode {//finish
    //ConstExp → AddExp
    AddExpNode addExpNode;

    public static ConstExpNode ConstExp() {
        ConstExpNode constExpNode = new ConstExpNode();
        AddExpNode addExpNode;
        addExpNode = AddExpNode.AddExp();
        if(addExpNode == null) {
            return null;
        }
        constExpNode.addExpNode = addExpNode;
        return constExpNode;
    }

    void print() {
        addExpNode.print(true);
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<ConstExp>";
    }

    private ConstExpNode() {}
}
