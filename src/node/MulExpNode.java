package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class MulExpNode {//finish
    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    // change it the same as AddExpNode
    UnaryExpNode unaryExpNode;
    Token mulToken;
    ExpInfo expInfo;
    MulExpNode shorterMulExpNode;

    static MulExpNode MulExp() {
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

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2;
        UnaryExpNode innerUnaryExpNode;
        MulExpNode innerMulExpNode = shorterMulExpNode;
        Token innerMulToken = mulToken;
        unaryExpNode.makeLLVM();
        expInfo = unaryExpNode.expInfo;
        innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        while (innerUnaryExpNode != null) {
            innerUnaryExpNode.makeLLVM();
            expInfo2 = innerUnaryExpNode.expInfo;
            expInfo.regIndex = llvmGenerator.makeCalculate(innerMulToken, true,  expInfo.regIndex, true, expInfo2.regIndex);
            innerMulToken = innerMulExpNode == null ? null : innerMulExpNode.mulToken;
            innerMulExpNode = innerMulExpNode.shorterMulExpNode;
            innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        }
    }

    int calculateConstExp() throws ExpNotConstException {
        int ans = unaryExpNode.calculateConstExp();
        UnaryExpNode innerUnaryExpNode;
        MulExpNode innerMulExpNode = shorterMulExpNode;
        Token innerMulToken = mulToken;
        innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        while (innerUnaryExpNode != null) {
            if (innerMulToken.getType().equals(TokenType.MULT) == true) {
                ans *= innerUnaryExpNode.calculateConstExp();
            } else if (innerMulToken.getType().equals(TokenType.DIV) == true) {
                ans /= innerUnaryExpNode.calculateConstExp();
            } else {
                ans %= innerUnaryExpNode.calculateConstExp();
            }
            innerMulToken = innerMulExpNode == null ? null : innerMulExpNode.mulToken;
            innerMulExpNode = innerMulExpNode.shorterMulExpNode;
            innerUnaryExpNode = innerMulExpNode == null ? null : innerMulExpNode.unaryExpNode;
        }
        return ans;
    }

    @Override
    public String toString() {
        return "<MulExp>";
    }

    private MulExpNode() {}
}
