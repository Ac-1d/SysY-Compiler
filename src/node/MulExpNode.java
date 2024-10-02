package node;

public class MulExpNode {
    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp

    public static MulExpNode MulExp() {
        MulExpNode mulExpNode = new MulExpNode();

        return mulExpNode;
    }
}
