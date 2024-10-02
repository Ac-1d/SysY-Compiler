package node;

public class LAndExpNode {
    // LAndExp → EqExp | LAndExp '&&' EqExp

    public static LAndExpNode LAndExp() {
        LAndExpNode lAndExpNode = new LAndExpNode();

        return lAndExpNode;
    }
}
