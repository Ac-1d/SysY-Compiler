package node;

import java.util.ArrayList;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class ConstInitValNode {//finish
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    /**in case 1 or 2 */
    ConstExpNode constExpNode;
    Token lbraceToken;
    ArrayList<InitArrayNode> initArrayNodesList = new ArrayList<>();
    Token rbraceToken;
    Token stringConstToken;
    
    public static ConstInitValNode ConstInitVal() {
        Parse instance = Parse.getInstance();
        ConstInitValNode constInitValNode = new ConstInitValNode();
        ConstExpNode constExpNode;
        InitArrayNode initArrayNode;
        Token token;
        int tmpIndex;
        //case 1
        tmpIndex = instance.getPeekIndex();
        constExpNode = ConstExpNode.ConstExp();
        if(constExpNode != null) {
            constInitValNode.constExpNode = constExpNode;
            return constInitValNode;
        }
        instance.setPeekIndex(tmpIndex);
        //case 2
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        constInitValNode.lbraceToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.LBRACE) == true) {//吃到了 '{'，一定是case2，不必顾虑tmpIndex覆盖问题
            tmpIndex = instance.getPeekIndex();
            constExpNode = ConstExpNode.ConstExp();
            if(constExpNode == null) {//不包含'[]'
                instance.setPeekIndex(tmpIndex);
            }
            else {
                tmpIndex = instance.getPeekIndex();
                while((initArrayNode = InitArrayNode.InitArray()) != null) {
                    tmpIndex = instance.getPeekIndex();
                    constInitValNode.initArrayNodesList.add(initArrayNode);
                }
                instance.setPeekIndex(tmpIndex);
            }
            token = instance.peekNextToken();
            constInitValNode.rbraceToken.setLineNum(token.getLineNum());//不存在右花括号缺失的情况
            return constInitValNode;
        }
        //case 3
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        constInitValNode.stringConstToken.setLineNum(token.getLineNum());
        constInitValNode.stringConstToken.setWord(token.getWord());
        if(token.getType().equals(TokenType.STRCON) == true) {
            return constInitValNode;
        }
        return null;
    }

    private ConstInitValNode() {
        this.lbraceToken = new Token(TokenType.LBRACE, "{");
        this.rbraceToken = new Token(TokenType.RBRACE, "}");
        this.stringConstToken = new Token(TokenType.STRCON, null);
    }

    class InitArrayNode {//finish
        // InitArray → ',' ConstExp
        Token commaToken;
        ConstExpNode constExpNode;
        public static InitArrayNode InitArray() {
            Parse instance = Parse.getInstance();
            InitArrayNode initArrayNode = (new ConstInitValNode()).new InitArrayNode();
            ConstExpNode constExpNode;
            Token token;
            token = instance.peekNextToken();
            initArrayNode.commaToken.setLineNum(token.getLineNum());
            if(token.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            constExpNode = ConstExpNode.ConstExp();
            if(constExpNode == null) {
                return null;
            }
            initArrayNode.constExpNode = constExpNode;
            return initArrayNode;
        }

    }

}
