package node;

import frontend.Parser;

public class DeclNode {//finish
    // Decl → ConstDecl | VarDecl
    // dont print

    ConstDeclNode constDeclNode;
    VarDeclNode varDeclNode;
    
    public static DeclNode Decl() {
        Parser instance = Parser.getInstance();
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

    void print() {
        if(constDeclNode != null) {
            constDeclNode.print();
        }
        else {
            varDeclNode.print();
        }
    }

    void setupSymbolTable() {
        if(constDeclNode != null) {
            constDeclNode.setupSymbolTable();
        }
        else {
            varDeclNode.setupSymbolTable();
        }
    }

    void makeLLVM() {
        
    }

    private DeclNode() {}

}
