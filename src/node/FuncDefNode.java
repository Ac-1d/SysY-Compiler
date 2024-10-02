package node;

public class FuncDefNode {
    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block

    public static FuncDefNode FuncDef() {
        FuncDefNode funcDefNode = new FuncDefNode();

        return funcDefNode;
    }
    
}
