package node;

public class RelExpNode {
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    public static RelExpNode RelExp() {
        RelExpNode relExpNode = new RelExpNode();

        return relExpNode;
    }
}
