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
            defArrayNode.lbrackToken.print();
            defArrayNode.constExpNode.print();
            defArrayNode.rbrackToken.print();
        }
        if(initValNode != null) {
            assignToken.print();
            initValNode.print();
        }
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<VarDef>";
    }

    private VarDefNode() {
        assignToken = new Token(TokenType.ASSIGN, "=");
    }

    class DefArrayNode {//finish
        Token lbrackToken;
        ConstExpNode constExpNode;
        Token rbrackToken;

        public static DefArrayNode DefArray() {
            Parser instance = Parser.getInstance();
            DefArrayNode defArrayNode = (new VarDefNode()).new DefArrayNode();
            ConstExpNode constExpNode;
            Token token;
            int tmpIndex;
            token = instance.peekNextToken();
            defArrayNode.lbrackToken.setLineNum(token.getLineNum());
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
            lbrackToken = new Token(TokenType.LBRACK, "[");
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }

}
