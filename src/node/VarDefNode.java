package node;

import frontend.Parser;
import token.Token;
import token.TokenType;
import error.Error;

public class VarDefNode {//finish
    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal

    Token identToken;
    DefArrayNode defArrayNode;
    Token assignToken;
    InitValNode initValNode;

    public static VarDefNode VarDef() {
        Parser instance = Parser.getInstance();
        VarDefNode varDefNode = new VarDefNode();
        Token indentToken;
        DefArrayNode defArrayNode;
        Token assignToken;
        InitValNode initValNode;
        int tmpIndex;
        indentToken = instance.peekNextToken();
        if(indentToken.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        varDefNode.identToken = indentToken;
        tmpIndex = instance.getPeekIndex();
        defArrayNode = DefArrayNode.DefArray();
        if(defArrayNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        tmpIndex = instance.getPeekIndex();
        assignToken = instance.peekNextToken();
        if(assignToken.getType().equals(TokenType.ASSIGN) == false) {
            instance.setPeekIndex(tmpIndex);
            return varDefNode;
        }
        varDefNode.assignToken.setLineNum(assignToken.getLineNum());
        initValNode = InitValNode.InitVal();
        if(initValNode == null) {
            return null;
        }
        varDefNode.initValNode = initValNode;
        return varDefNode;
    }

    void print() {
        identToken.print();
        if(defArrayNode != null) {
            defArrayNode.LBRACK.print();
            defArrayNode.constExpNode.print();
            defArrayNode.RBRACK.print();
        }
        if(initValNode != null) {
            assignToken.print();
            initValNode.print();
        }
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<VarDefNode>";
    }

    private VarDefNode() {
        assignToken = new Token(TokenType.ASSIGN, "=");
    }

    class DefArrayNode {//finish
        Token LBRACK;
        expNode constExpNode;
        Token RBRACK;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new VarDefNode()).new DefArrayNode();
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

    }

}
