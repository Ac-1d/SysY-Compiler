package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class BlockNode {//finish
    // Block → '{' { BlockItem } '}'

    Token lbraceToken;
    ArrayList<BlockItemNode> blockItemNodesList = new ArrayList<>();
    Token rbraceToken;

    public static BlockNode Block() {
        Parser instance = Parser.getInstance();
        BlockNode blockNode = new BlockNode();
        Token lbraceToken;
        BlockItemNode blockItemNode;
        Token rbraceToken;
        int tmpIndex;
        lbraceToken = instance.peekNextToken();
        if(lbraceToken.getType().equals(TokenType.LBRACE) == false) {
            return null;
        }
        blockNode.lbraceToken.setLineNum(lbraceToken.getLineNum());
        tmpIndex = instance.getPeekIndex();
        while((blockItemNode = BlockItemNode.BlockItem()) != null) {
            tmpIndex = instance.getPeekIndex();
            blockNode.blockItemNodesList.add(blockItemNode);
        }
        instance.setPeekIndex(tmpIndex);
        rbraceToken = instance.peekNextToken();
        if(rbraceToken.getType().equals(TokenType.RBRACE) == false) {
            return null;
        }
        blockNode.rbraceToken.setLineNum(rbraceToken.getLineNum());
        return blockNode;
    }

    void print() {
        lbraceToken.print();
        for (BlockItemNode blockItemNode : blockItemNodesList) {
            blockItemNode.print();
        }
        rbraceToken.print();
        System.out.println(this.toString());
    }

    public String toString() {
        return "<Block>";
    }

    private BlockNode() {
        lbraceToken = new Token(TokenType.LBRACE, "{");
        rbraceToken = new Token(TokenType.RBRACE, "}");
    }
}
