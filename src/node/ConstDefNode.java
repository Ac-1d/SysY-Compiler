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
        constDefNode.identToken = token;
        if(token.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        tmpIndex = instance.getPeekIndex();
        defArrayNode = ConstDefNode.DefArrayNode.DefArray();
        constDefNode.defArrayNode = defArrayNode;
        if(defArrayNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        token = instance.peekNextToken();
        constDefNode.assignToken = token;
        if(token.getType().equals(TokenType.ASSIGN) == false) {
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
            defArrayNode.lbrackToken.print();
            defArrayNode.constExpNode.print();
            defArrayNode.rbrackToken.print();
        }
        assignToken.print();
        constInitValNode.print();
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<ConstDef>";
    }

    private ConstDefNode() {}

    class DefArrayNode {//finish
        Token lbrackToken;
        ConstExpNode constExpNode;
        Token rbrackToken;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new ConstDefNode()).new DefArrayNode();
            ConstExpNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.lbrackToken = token;
            if(token.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            constExpNode = ConstExpNode.ConstExp();
            defArrayNode.constExpNode = constExpNode;
            if(constExpNode == null) {
                return null;
            }
            token = instance.peekNextToken();
            defArrayNode.rbrackToken.setLineNum(token.getLineNum());
            tmpIndex = instance.getPeekIndex();
            if(token.getType().equals(TokenType.RBRACK) == false) {//未识别到']'
                instance.errorsList.add(new Error("Parse", instance.getPreTokenLineNum(token), 'k'));
                instance.setPeekIndex(tmpIndex);
            }
            else {
                defArrayNode.rbrackToken.setLineNum(token.getLineNum());
            }
            return defArrayNode;
        }

        private DefArrayNode() {
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }
}
