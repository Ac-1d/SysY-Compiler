package frontend;

import java.util.ArrayList;
import token.Token;

import error.Error;
import node.CompUnitNode;

public class Parse {
    private static final Parse instance = new Parse();
    private ArrayList<Token> tokensList;
    public ArrayList<Error> errorsList;
    private int index;
    private int peekIndex;
    public static Parse getInstance() {
        return instance;
    }

    public void init() {
        Lexer instance = Lexer.getInstace();
        this.tokensList = instance.getTokensList();
        this.errorsList = instance.getErrorsList();
        this.index = 0;
        this.peekIndex = 0;
    }

    public void parseAnalyse() {
        init();
        CompUnitNode.CompUnit();
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
            token = null;
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
}
