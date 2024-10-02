package node;

public class InitValNode {
    // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst

    public static InitValNode InitVal() {
        InitValNode initValNode = new InitValNode();

        return initValNode;
    }
}
