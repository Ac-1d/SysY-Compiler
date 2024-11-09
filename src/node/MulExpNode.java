package node;

import Symbol.ExpInfo;
import Symbol.LLVMToken.LLVMToken;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class MulExpNode {//finish
    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    // change it the same as AddExpNode

    UnaryExpNode unaryExpNode;
    Token mulToken;
    MulExpNode shorterMulExpNode;

    public static MulExpNode MulExp() {
        Parser instance = Parser.getInstance();
        MulExpNode mulExpNode = new MulExpNode();
        UnaryExpNode unaryExpNode;
        Token mulToken;
        MulExpNode shorterMulExpNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        unaryExpNode = UnaryExpNode.UnaryExp();
        if(unaryExpNode == null) {
            instance.setPeekIndex(tmpIndex);
            return null;
        }
        mulExpNode.unaryExpNode = unaryExpNode;
        tmpIndex = instance.getPeekIndex();
        mulToken = instance.peekNextToken();
        TokenType tmpTokenType = mulToken.getType();
        if(tmpTokenType.equals(TokenType.MULT) == false && tmpTokenType.equals(TokenType.DIV) == false && tmpTokenType.equals(TokenType.MOD) == false) {
            instance.setPeekIndex(tmpIndex);
            return mulExpNode;
        }
        mulExpNode.mulToken = mulToken;
        shorterMulExpNode = MulExpNode.MulExp();
        mulExpNode.shorterMulExpNode = shorterMulExpNode;
        return mulExpNode;
    }

    void print() {
        unaryExpNode.print();
        System.out.println(toString());
        if(shorterMulExpNode != null) {
            mulToken.print();
            shorterMulExpNode.print();
        }
    }

    ExpInfo makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo, expInfo2;
        UnaryExpNode innerUnaryExpNode;
        MulExpNode innerMulExpNode = shorterMulExpNode;
        Token innerMulToken = mulToken;
        expInfo = unaryExpNode.makeLLVM();
        innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        while (innerUnaryExpNode != null) {
            expInfo2 = innerUnaryExpNode.makeLLVM();
            expInfo.regIndex = llvmGenerator.makeCalculate(innerMulToken, new LLVMToken(expInfo), new LLVMToken(expInfo2));
            innerMulToken = innerMulExpNode == null ? null : innerMulExpNode.mulToken;
            innerMulExpNode = innerMulExpNode.shorterMulExpNode;
            innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        }
        return expInfo;
    }

    @Override
    public String toString() {
        return "<MulExp>";
    }

    private MulExpNode() {}
}
