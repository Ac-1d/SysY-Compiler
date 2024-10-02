package node;

public class UnaryExpNode {
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp

    public static UnaryExpNode UnaryExp() {
        UnaryExpNode unaryExpNode = new UnaryExpNode();

        return unaryExpNode;
    }
}
