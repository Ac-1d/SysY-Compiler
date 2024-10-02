package node;

import frontend.Parse;

public class DeclNode {//finish
    //Decl → ConstDecl | VarDecl
    private ConstDeclNode constDeclNode;
    private VarDeclNode varDeclNode;
    
    public static DeclNode Decl() {
        Parse instance = Parse.getInstance();
        DeclNode decl = new DeclNode();
        ConstDeclNode constDeclNode;
        VarDeclNode varDeclNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        constDeclNode = ConstDeclNode.constDecl();
        if(constDeclNode != null) {//识别成功
            decl.constDeclNode = constDeclNode;
            return decl;
        }
        //(否则识别失败)，应回溯
        instance.setPeekIndex(tmpIndex);
        varDeclNode = VarDeclNode.VarDecl();
        if(varDeclNode != null) {
            decl.varDeclNode = varDeclNode;
            return decl;
        }
        //同上
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    private DeclNode() {}

}
