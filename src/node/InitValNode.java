package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;

//发现一些类可以进一步抽象，尝试使用多态来实现，等待后续的重构吧😪
public class InitValNode {//finish maybe some mistake
    // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
    /**in case 1 or 2 */
    ExpNode expNode;
    Token lbraceToken;
    ArrayList<InitArrayNode> initArrayNodesList = new ArrayList<>();
    Token rbraceToken;
    Token stringConstToken;
    int state;
    Integer expValue;
    ExpInfo expInfo = new ExpInfo();
    
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
            initValNode.state = 1;
            return initValNode;
        }
        instance.setPeekIndex(tmpIndex);
        //case 2
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.LBRACE) == true) {//吃到了 '{'，一定是case2，不必顾虑tmpIndex覆盖问题
            initValNode.lbraceToken.setLineNum(token.getLineNum());
            tmpIndex = instance.getPeekIndex();
            expNode = ExpNode.Exp();
            if(expNode == null) {//不包含'[]'
                instance.setPeekIndex(tmpIndex);
            }
            else {
                initValNode.expNode = expNode;
                tmpIndex = instance.getPeekIndex();
                while((initArrayNode = InitArrayNode.InitArray()) != null) {
                    tmpIndex = instance.getPeekIndex();
                    initValNode.initArrayNodesList.add(initArrayNode);
                }
                instance.setPeekIndex(tmpIndex);
            }
            token = instance.peekNextToken();
            initValNode.rbraceToken.setLineNum(token.getLineNum());//不存在右花括号缺失的情况
            initValNode.state = 2;
            return initValNode;
        }
        //case 3
        instance.setPeekIndex(tmpIndex);
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        initValNode.stringConstToken.setLineNum(token.getLineNum());
        initValNode.stringConstToken.setWord(token.getWord());
        if(token.getType().equals(TokenType.STRCON) == true) {
            initValNode.state = 3;
            return initValNode;
        }
        return null;
    }

    void print() {
        switch (state) {
            case 1:
                expNode.print();
                break;
            case 2:
                lbraceToken.print();
                if(expNode != null) {
                    expNode.print();
                    for (InitArrayNode initArrayNode : initArrayNodesList) {
                        initArrayNode.commaToken.print();
                        initArrayNode.expNode.print();
                    }
                }
                rbraceToken.print();
                break;
            case 3:
                stringConstToken.print();
                break;
            default:
                break;
        }
        System.out.println(toString());
    }

    void makeLLVM() {
        switch (state) {
            case 1:
                //逻辑为：如果能算出值，按常量处理，否则展开语法树进行分析
                try {
                    expValue = Integer.valueOf(expNode.calculateConstExp());
                } catch (ExpNotConstException e) {
                    expValue = null;
                    expNode.makeLLVM();
                    expInfo = expNode.expInfo;
                }
                break;
            case 2:
                try {
                    expValue = Integer.valueOf(expNode.calculateConstExp());
                } catch (ExpNotConstException e) {
                    expValue = null;
                    expNode.makeLLVM();
                }
                for (InitArrayNode initArrayNode : initArrayNodesList) {
                    try {
                        initArrayNode.expValue = Integer.valueOf(initArrayNode.expNode.calculateConstExp());
                    } catch (ExpNotConstException e) {
                        initArrayNode.expValue = null;
                        initArrayNode.expNode.makeLLVM();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "<InitVal>";
    }

    private InitValNode() {
        this.lbraceToken = new Token(TokenType.LBRACE, "{");
        this.rbraceToken = new Token(TokenType.RBRACE, "}");
        this.stringConstToken = new Token(TokenType.STRCON, null);
    }

    class InitArrayNode {//finish
        // InitArray → ',' ConstExp
        Token commaToken;
        ExpNode expNode;
        Integer expValue;

        public static InitArrayNode InitArray() {
            Parser instance = Parser.getInstance();
            InitArrayNode initArrayNode = (new InitValNode()).new InitArrayNode();
            ExpNode expNode;
            Token token;
            token = instance.peekNextToken();
            initArrayNode.commaToken = token;
            if(token.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            expNode = ExpNode.Exp();
            if(expNode == null) {
                return null;
            }
            initArrayNode.expNode = expNode;
            return initArrayNode;
        }
    }

}
