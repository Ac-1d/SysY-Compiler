package node;

import error.Error;
import frontend.Parser;
import token.Token;
import token.TokenType;
import java.util.ArrayList;

public class LValNode {//finish
    // LVal → Ident ['[' Exp ']'] 

    Token identToken;
    ArrayList<ArrayNode> arrayNodeslList = new ArrayList<>();

    public static LValNode LVal() {
        Parser instance = Parser.getInstance();
        LValNode lValNode = new LValNode();
        Token identToken;
        ArrayNode arrayNode;
        int tmpIndex;
        identToken = instance.peekNextToken();
        if(identToken.getType().equals(TokenType.IDENFR) == false) {
            return null;
        }
        lValNode.identToken = identToken;
        tmpIndex = instance.getPeekIndex();
        while((arrayNode = ArrayNode.Array()) != null) {
            tmpIndex = instance.getPeekIndex();
            lValNode.arrayNodeslList.add(arrayNode);
        }
        instance.setPeekIndex(tmpIndex);
        return lValNode;
    }

    class ArrayNode {
        // ArrayNode → '[' Exp ']'

        Token lbrackToken;
        ExpNode expNode;
        Token rbrackToken;

        public static ArrayNode Array() {
            Parser instance = Parser.getInstance();
            ArrayNode arrayNode = (new LValNode()).new ArrayNode();
            Token lbrackToken;
            ExpNode expNode;
            Token rbrackToken;
            lbrackToken = instance.peekNextToken();
            if(lbrackToken.getType().equals(TokenType.LBRACK) == false) {
                return null;
            }
            arrayNode.lbrackToken.setLineNum(lbrackToken.getLineNum());
            expNode = ExpNode.Exp();
            if(expNode == null) {
                return null;
            }
            arrayNode.expNode = expNode;
            rbrackToken = instance.peekNextToken();
            if(rbrackToken.getType().equals(TokenType.RBRACK) == false) {
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(rbrackToken), 'k'));
            }
            arrayNode.rbrackToken.setLineNum(rbrackToken.getLineNum());
            return arrayNode;
        }

        private ArrayNode() {
            lbrackToken = new Token(TokenType.LBRACK, "[");
            rbrackToken = new Token(TokenType.RBRACK, "]");
        }

    }
}
