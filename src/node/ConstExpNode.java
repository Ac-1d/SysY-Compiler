package node;

import Exception.ExpNotConstException;

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
        addExpNode.print();
        System.out.println(toString());
    }

    //ConstExpNode不会出现该异常，故不做处理
    int calculateConstExp() {
        try {
            return addExpNode.calculateConstExp(true);
        } catch (ExpNotConstException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "<ConstExp>";
    }

    private ConstExpNode() {}
}
