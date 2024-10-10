package node;

import error.Error;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class ConstDefNode {//finish
    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    // 此处不检查数组/变量声明与数组/变量初始化是否匹配
    Token identToken;
    DefArrayNode defArrayNode;
    Token assignToken;
    ConstInitValNode constInitValNode;
    
    public static ConstDefNode ConstDef() {
        Parser instance = Parser.getInstance();
        ConstDefNode constDefNode = new ConstDefNode();
        ConstDefNode.DefArrayNode defArrayNode;
        ConstInitValNode constInitValNode;
        Token token;
        int tmpIndex;
        token = instance.peekNextToken();
        constDefNode.identToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        tmpIndex = instance.getPeekIndex();
        defArrayNode = ConstDefNode.DefArrayNode.DefArray();
        constDefNode.defArrayNode = defArrayNode;
        if(defArrayNode == null) {
            instance.setPeekIndex(tmpIndex);
            return null;
        }
        token = instance.peekNextToken();
        constDefNode.assignToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.ASSIGN)) {
            return null;
        }
        constInitValNode = ConstInitValNode.ConstInitVal();
        constDefNode.constInitValNode = constInitValNode;
        if(constInitValNode == null) {
            return null;
        }
        
        return constDefNode;
    }

    void print() {
        identToken.print();
        if(defArrayNode != null) {
            defArrayNode.LBRACK.print();
            defArrayNode.constExpNode.print();
            defArrayNode.RBRACK.print();
        }
        assignToken.print();
        constInitValNode.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<ConstDefNode>";
    }

    private ConstDefNode() {}

    class DefArrayNode {//finish
        Token LBRACK;
        expNode constExpNode;
        Token RBRACK;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new ConstDefNode()).new DefArrayNode();
            expNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.LBRACK.setLineNum(token.getLineNum());
            if(token.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            constExpNode = expNode.ConstExp();
            defArrayNode.constExpNode = constExpNode;
            if(constExpNode == null) {
                return null;
            }
            token = instance.peekNextToken();
            defArrayNode.RBRACK.setLineNum(token.getLineNum());
            tmpIndex = instance.getPeekIndex();
            if(token.getType().equals(TokenType.RBRACK) == false) {//未识别到']'
                instance.errorsList.add(new Error("Parse", instance.getPreTokenLineNum(token), 'k'));
                instance.setPeekIndex(tmpIndex);
            }
            else {
                defArrayNode.RBRACK.setLineNum(token.getLineNum());
            }
            return defArrayNode;
        }

        private DefArrayNode() {}

    }
}
