package node;

import frontend.Parse;

public class BlockItemNode {//finish
    // BlockItem → Decl | Stmt

    DeclNode declNode;
    StmtNode stmtNode;

    public static BlockItemNode BlockItem() {
        Parse instance = Parse.getInstance();
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
        if(stmtNode == null) {
            blockItemNode.stmtNode = stmtNode;
            return blockItemNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }
}
