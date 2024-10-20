package node;

import frontend.ErrorHandler;
import frontend.Parser;
import frontend.SymbolHandler;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

import Symbol.SymbolTable;
import error.ErrorType;
import error.Error;

public class BlockNode {//finish
    // Block → '{' { BlockItem } '}'
    // 当前文法满足一个确定的Block代表着一个完整的不会失败回溯的识别。我们域的划分建立在这个前提下。

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

    /**@param isFunc 
     *  该block是否属于func的block
    */
    void setupSymbolTable(boolean isFunc) {
        SymbolHandler instance = SymbolHandler.getInstance();
        if(isFunc == false) {
            instance.setCurSymbolTable(new SymbolTable(instance.getCurSymbolTable()));
        }
        for (BlockItemNode blockItemNode : blockItemNodesList) {
            blockItemNode.setupSymbolTable();
        }
        if(isFunc == false) {
            instance.setCurSymbolTable(instance.getCurSymbolTable().getFatherSymbolTable());
        }
    }

    void checkFuncHasReturn() {
        if (blockItemNodesList.size() == 0) {
            ErrorHandler.getInstance().addError(new Error(rbraceToken.getLineNum(), ErrorType.g));
            return;
        }
        BlockItemNode blockItemNode = blockItemNodesList.get(blockItemNodesList.size() - 1);
        if (blockItemNode.stmtNode != null) {
            if (blockItemNode.stmtNode.state == 7) {// end with return
                return;
            }
        }
        ErrorHandler.getInstance().addError(new Error(rbraceToken.getLineNum(), ErrorType.g));
    }

    public String toString() {
        return "<Block>";
    }

    private BlockNode() {
        lbraceToken = new Token(TokenType.LBRACE, "{");
        rbraceToken = new Token(TokenType.RBRACE, "}");
    }
}
