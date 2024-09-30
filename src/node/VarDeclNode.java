package node;

public class VarDeclNode {
    //VarDecl → BType VarDef { ',' VarDef } ';'

    public static VarDeclNode VarDecl() {
        VarDeclNode varDeclNode = new VarDeclNode();
        
        return varDeclNode;
    }
}
