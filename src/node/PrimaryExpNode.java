package node;

import error.Error;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class PrimaryExpNode {//finish
    // PrimaryExp → '(' Exp ')' | LVal | Number | Character

    Token lparentToken;
    ExpNode expNode;
    Token rparentToken;
    LValNode lValNode;
    NumberNode numberNode;
    CharacterNode characterNode;

    public static PrimaryExpNode primaryExp() {
        Parser instance = Parser.getInstance();
        PrimaryExpNode primaryExpNode = new PrimaryExpNode();
        Token lparentToken;
        ExpNode expNode;
        Token rparentToken;
        LValNode lValNode;
        NumberNode numberNode;
        CharacterNode characterNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        lparentToken = instance.peekNextToken();
        if(lparentToken.getType().equals(TokenType.LPARENT) == true) {
            primaryExpNode.lparentToken.setLineNum(lparentToken.getLineNum());
            expNode = ExpNode.Exp();
            if(expNode == null) {
                instance.setPeekIndex(tmpIndex);
                return null;
            }
            primaryExpNode.expNode = expNode;
            rparentToken = instance.peekNextToken();
            if(rparentToken.getType().equals(TokenType.RPARENT) == false) {
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(rparentToken), 'j'));
            }
            primaryExpNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        lValNode = LValNode.LVal();
        if(lValNode != null) {
            primaryExpNode.lValNode = lValNode;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        numberNode = NumberNode.Number();
        if(numberNode != null) {
            primaryExpNode.numberNode = numberNode;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        characterNode = CharacterNode.Character();
        if(characterNode != null) {
            primaryExpNode.characterNode = characterNode;
            return primaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    void print() {
        if(expNode != null) {
            lparentToken.print();
            expNode.print();
            rparentToken.print();
        }
        else if(lValNode != null) {
            lValNode.print();
        }
        else if(numberNode != null) {
            numberNode.print();
        }
        else if(characterNode != null) {
            characterNode.print();
        }
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<PrimaryExp>";
    }

    private PrimaryExpNode() {
        lparentToken = new Token(TokenType.LPARENT, "(");
        rparentToken = new Token(TokenType.RPARENT, ")");
    }
}
