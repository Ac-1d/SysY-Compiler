package node;

import error.Error;
import frontend.Parse;
import token.Token;
import token.TokenType;

public class ConstDefNode {//finish
    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    // 此处不检查数组/变量声明与数组/变量初始化是否匹配
    Token identToken;
    DefArrayNode defArrayNode;
    Token eqlToken;
    ConstInitValNode constInitValNode;
    
    public static ConstDefNode ConstDef() {
        Parse instance = Parse.getInstance();
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
        constDefNode.eqlToken.setLineNum(token.getLineNum());
        if(token.getType().equals(TokenType.EQL)) {
            return null;
        }
        constInitValNode = ConstInitValNode.ConstInitVal();
        constDefNode.constInitValNode = constInitValNode;
        if(constInitValNode == null) {
            return null;
        }
        
        return constDefNode;
    }

    class DefArrayNode {//finish
        Token LBRACK;
        ConstExpNode constExpNode;
        Token RBRACK;

        public static DefArrayNode DefArray() {
            Parse instance = Parse.getInstance();
            DefArrayNode defArrayNode = (new ConstDefNode()).new DefArrayNode();
            ConstExpNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.LBRACK.setLineNum(token.getLineNum());
            if(token.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            constExpNode = ConstExpNode.ConstExp();
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

    }
}
