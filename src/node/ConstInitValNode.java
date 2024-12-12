package node;

import java.util.ArrayList;
import java.util.List;

import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class ConstInitValNode {//finish
    // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    /**in case 1 or 2 */
    ConstExpNode constExpNode;
    Token lbraceToken;
    ArrayList<InitArrayNode> initArrayNodesList = new ArrayList<>();
    Token rbraceToken;
    Token strconToken;
    int strconTokenNum;
    int state;
    int constExpValue;
    List<ExpInfo> expInfos = new ArrayList<>();
    
    public static ConstInitValNode ConstInitVal() {
        Parser instance = Parser.getInstance();
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
            constInitValNode.state = 1;
            return constInitValNode;
        }
        instance.setPeekIndex(tmpIndex);
        //case 2
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.LBRACE) == true) {//吃到了 '{'，一定是case2，不必顾虑tmpIndex覆盖问题
            constInitValNode.lbraceToken.setLineNum(token.getLineNum());
            tmpIndex = instance.getPeekIndex();
            constExpNode = ConstExpNode.ConstExp();
            if(constExpNode == null) {//不包含'[]'
                instance.setPeekIndex(tmpIndex);
            }
            else {
                constInitValNode.constExpNode = constExpNode;
                tmpIndex = instance.getPeekIndex();
                while((initArrayNode = InitArrayNode.InitArray()) != null) {
                    tmpIndex = instance.getPeekIndex();
                    constInitValNode.initArrayNodesList.add(initArrayNode);
                }
                instance.setPeekIndex(tmpIndex);
            }
            token = instance.peekNextToken();
            constInitValNode.rbraceToken.setLineNum(token.getLineNum());//不存在右花括号缺失的情况
            constInitValNode.state = 2;
            return constInitValNode;
        }
        //case 3
        instance.setPeekIndex(tmpIndex);
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        constInitValNode.strconToken.setLineNum(token.getLineNum());
        constInitValNode.strconToken.setWord(token.getWord());
        if(token.getType().equals(TokenType.STRCON) == true) {
            constInitValNode.state = 3;
            return constInitValNode;
        }
        return null;
    }

    void print() {
        switch (state) {
            case 1:
                constExpNode.print();
                break;
            case 2:
                lbraceToken.print();
                if (constExpNode != null) {
                    constExpNode.print();
                    for (InitArrayNode initArrayNode : initArrayNodesList) {
                        initArrayNode.commaToken.print();
                        initArrayNode.constExpNode.print();
                    }
                }
                rbraceToken.print();
                break;
            case 3:
                strconToken.print();
                break;
            default:
                break;
        }
        System.out.println(toString());
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        // 我们假想此处不需要任何符号表填写与错误处理
        switch (state) {
            case 1:
                // constExpNode.setupSymbolTable();
                constExpValue = constExpNode.calculateConstExp();
                // System.out.println("the constExp value is :" + constExpNode.calculateConstExp());
                break;
            case 2:
                if (constExpNode != null) {
                    // constExpNode.setupSymbolTable();
                    constExpValue = constExpNode.calculateConstExp();
                    expInfos.add(new ExpInfo(constExpValue));
                }
                for (InitArrayNode initArrayNode : initArrayNodesList) {
                    // initArrayNode.constExpNode.setupSymbolTable();
                    initArrayNode.constExpValue = initArrayNode.constExpNode.calculateConstExp();
                    expInfos.add(new ExpInfo(initArrayNode.constExpValue));
                }
                break;
            case 3:
                strconTokenNum = llvmGenerator.getStrconTokenNum();
                break;
        
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "<ConstInitVal>";
    }

    private ConstInitValNode() {
        this.lbraceToken = new Token(TokenType.LBRACE, "{");
        this.rbraceToken = new Token(TokenType.RBRACE, "}");
        this.strconToken = new Token(TokenType.STRCON, null);
    }

    class InitArrayNode {//finish
        // InitArray → ',' ConstExp
        Token commaToken;
        ConstExpNode constExpNode;
        int constExpValue;
        public static InitArrayNode InitArray() {
            Parser instance = Parser.getInstance();
            InitArrayNode initArrayNode = (new ConstInitValNode()).new InitArrayNode();
            ConstExpNode constExpNode;
            Token token;
            token = instance.peekNextToken();
            initArrayNode.commaToken = token;
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
