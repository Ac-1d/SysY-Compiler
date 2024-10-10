package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class FuncFParamsNode {//finish
    // FuncFParams → FuncFParam { ',' FuncFParam }

    FuncFParamNode funcFParamNode;
    ArrayList<FuncFParamWithCommaNode> funcFParamWithCommaNodesList = new ArrayList<>();

    public static FuncFParamsNode FuncFParams() {
        Parser instance = Parser.getInstance();
        FuncFParamsNode funcFparamsNode = new FuncFParamsNode();
        FuncFParamNode funcFParamNode;
        FuncFParamWithCommaNode funcFParamWithCommaNode;
        int tmpIndex;
        funcFParamNode = FuncFParamNode.FuncFParam();
        if(funcFParamNode == null) {
            return null;
        }
        funcFparamsNode.funcFParamNode = funcFParamNode;
        tmpIndex = instance.getPeekIndex();
        while((funcFParamWithCommaNode = FuncFParamWithCommaNode.FuncFParamWithComma()) != null) {
            tmpIndex = instance.getPeekIndex();
            funcFparamsNode.funcFParamWithCommaNodesList.add(funcFParamWithCommaNode);
        }
        instance.setPeekIndex(tmpIndex);
        return funcFparamsNode;
    }

    void print() {
        funcFParamNode.print();
        for (FuncFParamWithCommaNode funcFParamWithCommaNode : funcFParamWithCommaNodesList) {
            funcFParamWithCommaNode.commaToken.print();
            funcFParamWithCommaNode.funcFParamNode.print();
        }
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "<FuncFParamsNode>";
    }

    private FuncFParamsNode() {}

    class FuncFParamWithCommaNode {
        // FuncFParamWithComma → ',' FuncFParam

        Token commaToken;
        FuncFParamNode funcFParamNode;

        public static FuncFParamWithCommaNode FuncFParamWithComma() {
            Parser instance = Parser.getInstance();
            FuncFParamWithCommaNode funcFParamWithCommaNode = (new FuncFParamsNode()).new FuncFParamWithCommaNode();
            Token commaToken;
            FuncFParamNode funcFParamNode;
            commaToken = instance.peekNextToken();
            if(commaToken.getType().equals(TokenType.COMMA) == false) {
                return null;
            }
            funcFParamWithCommaNode.commaToken = commaToken;
            funcFParamNode = FuncFParamNode.FuncFParam();
            if(funcFParamNode == null) {
                return null;
            }
            funcFParamWithCommaNode.funcFParamNode = funcFParamNode;
            return funcFParamWithCommaNode;
        }

    }
}
