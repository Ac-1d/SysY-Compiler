package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

//TODO: 发现一些类可以进一步抽象，尝试使用多态来实现，等待后续的重构吧😪
public class InitValNode {//finish maybe some mistake
    // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
    /**in case 1 or 2 */
    ExpNode expNode;
    Token lbraceToken;
    ArrayList<InitArrayNode> initArrayNodesList = new ArrayList<>();
    Token rbraceToken;
    Token stringConstToken;
    
    public static InitValNode InitVal() {
        Parser instance = Parser.getInstance();
        InitValNode initValNode = new InitValNode();
        ExpNode expNode;
        InitArrayNode initArrayNode;
        Token token;
        int tmpIndex;
        //case 1
        tmpIndex = instance.getPeekIndex();
        expNode = ExpNode.Exp();
        if(expNode != null) {
            initValNode.expNode = expNode;
            return initValNode;
        }
        instance.setPeekIndex(tmpIndex);
        //case 2
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        initValNode.lbraceToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.LBRACE) == true) {//吃到了 '{'，一定是case2，不必顾虑tmpIndex覆盖问题
            tmpIndex = instance.getPeekIndex();
            expNode = ExpNode.Exp();
            if(expNode == null) {//不包含'[]'
                instance.setPeekIndex(tmpIndex);
            }
            else {
                tmpIndex = instance.getPeekIndex();
                while((initArrayNode = InitArrayNode.InitArray()) != null) {
                    tmpIndex = instance.getPeekIndex();
                    initValNode.initArrayNodesList.add(initArrayNode);
                }
                instance.setPeekIndex(tmpIndex);
            }
            token = instance.peekNextToken();
            initValNode.rbraceToken.setLineNum(token.getLineNum());//不存在右花括号缺失的情况
            return initValNode;
        }
        //case 3
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        initValNode.stringConstToken.setLineNum(token.getLineNum());
        initValNode.stringConstToken.setWord(token.getWord());
        if(token.getType().equals(TokenType.STRCON) == true) {
            return initValNode;
        }
        return null;
    }

    private InitValNode() {
        this.lbraceToken = new Token(TokenType.LBRACE, "{");
        this.rbraceToken = new Token(TokenType.RBRACE, "}");
        this.stringConstToken = new Token(TokenType.STRCON, null);
    }

    class InitArrayNode {//finish
        // InitArray → ',' ConstExp
        Token commaToken;
        ConstExpNode constExpNode;
        public static InitArrayNode InitArray() {
            Parser instance = Parser.getInstance();
            InitArrayNode initArrayNode = (new InitValNode()).new InitArrayNode();
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
