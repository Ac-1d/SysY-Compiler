package node;

public class LOrExpNode {
    // LOrExp → LAndExp | LOrExp '||' LAndExp

    public static LOrExpNode LOrExp() {
        LOrExpNode lOrExpNode = new LOrExpNode();

        return lOrExpNode;
    }
}
