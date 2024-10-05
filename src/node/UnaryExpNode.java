package node;

import error.Error;
import frontend.Parse;
import token.Token;
import token.TokenType;

public class UnaryExpNode {//finish
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp

    PrimaryExpNode primaryExpNode;
    Token identToken;
    Token lparentToken;
    FuncRParamsNode funcRParamsNode;
    Token rparentToken;
    UnaryOpNode unaryOpNode;
    UnaryExpNode shortreUnaryExpNode;

    public static UnaryExpNode UnaryExp() {
        Parse instance = Parse.getInstance();
        UnaryExpNode unaryExpNode = new UnaryExpNode();
        PrimaryExpNode primaryExpNode;
        Token identToken;
        Token lparentToken;
        FuncRParamsNode funcRParamsNode;
        Token rparentToken;
        UnaryOpNode unaryOpNode;
        UnaryExpNode shorterUnaryExpNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        primaryExpNode = PrimaryExpNode.primaryExp();
        if(primaryExpNode != null) {//case 1
            unaryExpNode.primaryExpNode = primaryExpNode;
            return unaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        identToken = instance.peekNextToken();
        if(identToken.getType().equals(TokenType.IDENFR)) {//case 2
            lparentToken = instance.peekNextToken();
            unaryExpNode.lparentToken.setLineNum(lparentToken.getLineNum());
            int ttmpIndex = instance.getPeekIndex();
            funcRParamsNode = FuncRParamsNode.FuncRParams();
            if(funcRParamsNode == null) {
                instance.setPeekIndex(ttmpIndex);
            }
            unaryExpNode.funcRParamsNode = funcRParamsNode;
            ttmpIndex = instance.getPeekIndex();
            rparentToken = instance.peekNextToken();
            if(rparentToken.getType().equals(TokenType.RPARENT) == false) {
                instance.errorsList.add(new Error("parse", instance.getPreTokenLineNum(rparentToken), 'j'));
                instance.setPeekIndex(tmpIndex);
            }
            unaryExpNode.rparentToken.setLineNum(instance.getPreTokenLineNum(rparentToken));
            return unaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        unaryOpNode = UnaryOpNode.UnaryOp();
        if(unaryOpNode != null) {
            shorterUnaryExpNode = UnaryExpNode.UnaryExp();
            unaryExpNode.shortreUnaryExpNode = shorterUnaryExpNode;
            return unaryExpNode;
        }
        instance.setPeekIndex(tmpIndex);
        return null;
    }

    private UnaryExpNode() {}
}
