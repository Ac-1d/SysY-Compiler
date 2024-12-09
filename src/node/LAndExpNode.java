package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class LAndExpNode {//finish
    // LAndExp → EqExp | LAndExp '&&' EqExp

    EqExpNode eqExpNode;
    LAndExpNode shorterLAndExpNode;
    Token andToken;
    ExpInfo expInfo = new ExpInfo();

    public static LAndExpNode LAndExp() {
        Parser instance = Parser.getInstance();
        LAndExpNode lAndExpNode = new LAndExpNode();
        EqExpNode eqExpNode;
        LAndExpNode shorterAndExpNode;
        Token andToken;
        int tmpIndex;
        eqExpNode = EqExpNode.EqExp();
        if(eqExpNode == null) {
            return null;
        }
        lAndExpNode.eqExpNode = eqExpNode;
        tmpIndex = instance.getPeekIndex();
        andToken = instance.peekNextToken();
        if(andToken.getType().equals(TokenType.AND) == false) {
            instance.setPeekIndex(tmpIndex);
            return lAndExpNode;
        }
        lAndExpNode.andToken = andToken;
        shorterAndExpNode = LAndExpNode.LAndExp();
        if(shorterAndExpNode == null) {
            return null;
        }
        lAndExpNode.shorterLAndExpNode = shorterAndExpNode;
        return lAndExpNode;
    }

    void print() {
        eqExpNode.print();
        System.out.println(toString());
        if(shorterLAndExpNode != null) {
            andToken.print();
            shorterLAndExpNode.print();
        }
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2 = new ExpInfo();
        LAndExpNode innerLAndExpNode = shorterLAndExpNode;
        EqExpNode innerEqExpNode = shorterLAndExpNode == null ? null : innerLAndExpNode.eqExpNode;
        Token innerToken = andToken;
        try {
            expInfo.setValue(eqExpNode.calculateConstExp());
        } catch (ExpNotConstException e) {
            eqExpNode.makeLLVM();
            expInfo = eqExpNode.expInfo;
        }
        while (innerEqExpNode != null) {
            try {
                expInfo2.setValue(innerEqExpNode.calculateConstExp());
            } catch (ExpNotConstException e) {
                innerEqExpNode.makeLLVM();
                expInfo2 = innerEqExpNode.expInfo;
            }
            expInfo.setReg(llvmGenerator.makeCalculate(innerToken, expInfo2, expInfo2));
            innerToken = innerLAndExpNode.andToken;
            innerLAndExpNode = innerLAndExpNode.shorterLAndExpNode;
            innerEqExpNode = innerLAndExpNode == null ? null : innerLAndExpNode.eqExpNode;
        }
    }

    boolean calculateConstExp() throws ExpNotConstException {
        boolean ans = eqExpNode.calculateConstExp();
        LAndExpNode innerLAndExpNode = shorterLAndExpNode;
        EqExpNode innerEqExpNode = innerLAndExpNode == null ? null : innerLAndExpNode.eqExpNode;
        while (innerEqExpNode != null) {
            ans = ans && innerEqExpNode.calculateConstExp();
            innerLAndExpNode = innerLAndExpNode.shorterLAndExpNode;
            innerEqExpNode = innerLAndExpNode == null ? null : innerLAndExpNode.eqExpNode;
        }
        return ans;
    }

    @Override
    public String toString() {
        return "<LAndExp>";
    }

    private LAndExpNode() {}
}
