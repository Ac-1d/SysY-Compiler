package frontend;

import java.util.ArrayList;
import java.util.Collections;

import config.Config;
import token.Token;
import token.TokenType;
import error.Error;
import node.CompUnitNode;

public class Parser {
    private static final Parser instance = new Parser();
    private ArrayList<Token> tokensList;
    public ArrayList<Error> errorsList;
    private int index;
    private int peekIndex;
    public CompUnitNode compUnitNode;

    public static Parser getInstance() {
        return instance;
    }

    private void init() {
        Config.parser();
        // System.setOut(Config.originalStream);
        Lexer instance = Lexer.getInstace();
        this.tokensList = instance.getTokensList();
        this.errorsList = instance.getErrorsList();
        this.index = 0;
        this.peekIndex = 0;
    }

    public void parseAnalyse() {
        init();
        compUnitNode = CompUnitNode.CompUnit();
        Collections.sort(errorsList);
    }

    public void print() {
        compUnitNode.print();
    }

    public void printError() {
        for (Error error : errorsList) {
            System.out.println(error.toString());
        }
    }


    //现状为在递归内部开的后门替代了index与peekIndex间的约束作用，暂时保留reset看看吧
    /**
     * peek逻辑为：
     * 只有peekNextToken可以读取Token
     * 在一个模块完成后根据是否成功读取(返回值是否为null)来决定ignore还是reset
     * 由peekNextToken()与reset()两个函数控制
     */
    public Token peekNextToken() {
        Token token;
        try {
            token = tokensList.get(peekIndex);
        } catch (Exception e) {
            token = new Token(TokenType.None, null);
        }
        peekIndex++;
        return token;
    }

    public void resetIndex() {
        index = peekIndex;
    }

    public void resetPeekIndex() {
        peekIndex = index;
    }
    
    /**
     * 考虑到[]与{}模块的回溯问题，为peekIndex开一个后门
     * 现在需要谨慎考虑reset()函数的位置
     */
    public int getPeekIndex() {
        return peekIndex;
    }

    public void setPeekIndex(int peekIndex) {
        this.peekIndex = peekIndex;
    }

    /**由于使用时不会寻找第一个token前一个的行号，故不对其进行特判 */
    public int getPreTokenLineNum(Token token) {
        return tokensList.get((tokensList.indexOf(token) - 1)).getLineNum();
    }
}
