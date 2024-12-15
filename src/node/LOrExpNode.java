package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class LOrExpNode {//finish
    // LOrExp → LAndExp | LOrExp '||' LAndExp
    
    LAndExpNode lAndExpNode;
    LOrExpNode shorterLOrExpNode;
    Token orToken;
    ExpInfo expInfo = new ExpInfo();

    public static LOrExpNode LOrExp() {
        Parser instance = Parser.getInstance();
        LOrExpNode lOrExpNode = new LOrExpNode();
        LAndExpNode lAndExpNode;
        LOrExpNode shoterLOrExpNode;
        Token orToken;
        int tmpIndex;
        lAndExpNode = LAndExpNode.LAndExp();
        if(lAndExpNode == null) {
            return null;
        }
        lOrExpNode.lAndExpNode = lAndExpNode;
        tmpIndex = instance.getPeekIndex();
        orToken = instance.peekNextToken();
        if(orToken.getType().equals(TokenType.OR) == false) {
            instance.setPeekIndex(tmpIndex);
            return lOrExpNode;
        }
        lOrExpNode.orToken = orToken;
        shoterLOrExpNode = LOrExpNode.LOrExp();
        if(shoterLOrExpNode == null) {
            return null;
        }
        lOrExpNode.shorterLOrExpNode = shoterLOrExpNode;
        return lOrExpNode;
    }

    void print() {
        lAndExpNode.print();
        System.out.println(toString());
        if(shorterLOrExpNode != null) {
            orToken.print();
            shorterLOrExpNode.print();
        }
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2 = new ExpInfo();
        LOrExpNode innerLOrExpNode = shorterLOrExpNode;
        LAndExpNode innerLAndExpNode = shorterLOrExpNode == null ? null : innerLOrExpNode.lAndExpNode;
        try {
            expInfo.setValue(lAndExpNode.calculateConstExp());
            if (expInfo.getValue() == 1) {
                return;
            }
        } catch (ExpNotConstException e) {
            lAndExpNode.makeLLVM();
            expInfo = lAndExpNode.expInfo;
        }
        while (innerLAndExpNode != null) {
            try {
                expInfo2.setValue(innerLAndExpNode.calculateConstExp());
                if (expInfo2.getValue() == 1) {
                    break;
                }
            } catch (ExpNotConstException e) {
                innerLAndExpNode.makeLLVM();
                expInfo2 = innerLAndExpNode.expInfo;
            }
            innerLOrExpNode = innerLOrExpNode.shorterLOrExpNode;
            innerLAndExpNode = innerLOrExpNode == null ? null : innerLOrExpNode.lAndExpNode;
        }
        llvmGenerator.removeLastOr();
    }

    boolean calculateConstExp() throws ExpNotConstException {
        boolean ans = lAndExpNode.calculateConstExp();
        LOrExpNode innerOrExpNode = shorterLOrExpNode;
        LAndExpNode innerAndExpNode = innerOrExpNode == null ? null : innerOrExpNode.lAndExpNode;
        while (innerAndExpNode != null) {
            ans = ans || innerAndExpNode.calculateConstExp();
            innerOrExpNode = innerOrExpNode.shorterLOrExpNode;
            innerAndExpNode = innerOrExpNode == null ? null : innerOrExpNode.lAndExpNode;
        }
        return ans;
    }

    @Override
    public String toString() {
        return "<LOrExp>";
    }

    private LOrExpNode() {}
}
