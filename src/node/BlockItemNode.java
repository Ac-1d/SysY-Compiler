package node;

import frontend.Parser;

public class BlockItemNode {//finish
    // BlockItem → Decl | Stmt
    // dont print

    DeclNode declNode;
    StmtNode stmtNode;

    public static BlockItemNode BlockItem() {
        Parser instance = Parser.getInstance();
        BlockItemNode blockItemNode = new BlockItemNode();
        DeclNode declNode;
        StmtNode stmtNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        declNode = DeclNode.Decl();
        if(declNode !=  null) {
            blockItemNode.declNode = declNode;
            return blockItemNode;
        }
        instance.setPeekIndex(tmpIndex);
        stmtNode = StmtNode.Stmt();
        if(stmtNode != null) {
            blockItemNode.stmtNode = stmtNode;
            return blockItemNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    void print() {
        if(declNode != null) {
            declNode.print();
        }
        else {
            stmtNode.print();
        }
    }

    void setupSymbolTable() {
        if(declNode != null) {
            declNode.setupSymbolTable();
        }
        else {
            stmtNode.setupSymbolTable();
        }
    }

    private BlockItemNode() {}
}
