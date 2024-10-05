package node;

import frontend.Parse;
import token.Token;
import token.TokenType;
import java.util.ArrayList;

public class FuncRParamsNode {//finish
    // FuncRParams → Exp { ',' Exp } 

    ExpNode expNode;
    ArrayList<ParamNode> paramNodesList = new ArrayList<>();

    public static FuncRParamsNode FuncRParams() {
        Parse instance = Parse.getInstance();
        FuncRParamsNode funcRParamsNode = new FuncRParamsNode();
        ExpNode expNode;
        ParamNode paramNode;
        int tmpIndex;
        expNode = ExpNode.Exp();
        if(expNode == null) {
            return null;
        }
        funcRParamsNode.expNode = expNode;
        tmpIndex = instance.getPeekIndex();
        while((paramNode = ParamNode.Param()) != null) {
            tmpIndex = instance.getPeekIndex();
            funcRParamsNode.paramNodesList.add(paramNode);
        }
        instance.setPeekIndex(tmpIndex);
        return funcRParamsNode;
    }

    private FuncRParamsNode() {}

    class ParamNode {
        // Param → ',' Exp

        Token commaToken;
        ExpNode expNode;

        public static ParamNode Param() {
            Parse instance = Parse.getInstance();
            ParamNode paramNode = (new FuncRParamsNode()).new ParamNode();
            Token commaToken;
            ExpNode expNode;
            commaToken = instance.peekNextToken();
            if(commaToken.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            paramNode.commaToken.setLineNum(commaToken.getLineNum());
            expNode = ExpNode.Exp();
            if(expNode == null) {
                return null;
            }
            paramNode.expNode = expNode;
            return paramNode;
        }

        private ParamNode() {
            this.commaToken = new Token(TokenType.COMMA, ",");
        }
    }
}
