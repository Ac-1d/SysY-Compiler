package node;

public class EqExpNode {
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    // change it the same as AddExp

    public static EqExpNode EqExp() {
        EqExpNode eqExpNode = new EqExpNode();

        return eqExpNode;
    }
}
