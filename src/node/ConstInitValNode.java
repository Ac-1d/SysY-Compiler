package node;

public class ConstInitValNode {
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    
    public static ConstInitValNode ConstInitVal() {
        ConstInitValNode constInitValNode = new ConstInitValNode();

        return constInitValNode;
    }

}
