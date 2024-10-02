package node;

public class EqExpNode {
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp

    public static EqExpNode EqExp() {
        EqExpNode eqExpNode = new EqExpNode();

        return eqExpNode;
    }
}
