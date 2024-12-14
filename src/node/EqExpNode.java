package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import Symbol.VarType;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class EqExpNode {//finish
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    // change it the same as AddExp

    RelExpNode relExpNode;
    EqExpNode shorterEqExpNode;
    Token eqlOrNeqToken;
    ExpInfo expInfo = new ExpInfo();

    public static EqExpNode EqExp() {
        Parser instance = Parser.getInstance();
        EqExpNode eqExpNode = new EqExpNode();
        RelExpNode relExpNode;
        EqExpNode shorterEqExpNode;
        Token eqlOrNeqToken;
        int tmpIndex;
        relExpNode = RelExpNode.RelExp();
        if(relExpNode == null) {
            return null;
        }
        eqExpNode.relExpNode = relExpNode;
        tmpIndex = instance.getPeekIndex();
        eqlOrNeqToken = instance.peekNextToken();
        if(eqlOrNeqToken.getType().equals(TokenType.EQL) == false && eqlOrNeqToken.getType().equals(TokenType.NEQ) == false) {
            instance.setPeekIndex(tmpIndex);
            return eqExpNode;
        }
        eqExpNode.eqlOrNeqToken = eqlOrNeqToken;
        tmpIndex = instance.getPeekIndex();
        shorterEqExpNode = EqExpNode.EqExp();
        if(shorterEqExpNode == null) {
            instance.setPeekIndex(tmpIndex);
        }
        else {
            eqExpNode.shorterEqExpNode = shorterEqExpNode;
        }
        return eqExpNode;
    }

    void print() {
        relExpNode.print();
        System.out.println(toString());
        if(shorterEqExpNode != null) {
            eqlOrNeqToken.print();
            shorterEqExpNode.print();
        }
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2 = new ExpInfo();
        EqExpNode innerEqExpNode = shorterEqExpNode;
        RelExpNode innerRelExpNode = innerEqExpNode == null ? null : innerEqExpNode.relExpNode;
        Token innerToken = eqlOrNeqToken;
        try {
            expInfo.setValue(relExpNode.calculateConstExp());
        } catch (ExpNotConstException e) {
            relExpNode.makeLLVM();
            expInfo = relExpNode.expInfo;
        }
        while (innerRelExpNode != null) {
            try {
                expInfo2.setValue(innerRelExpNode.calculateConstExp());
            } catch (ExpNotConstException e) {
                innerRelExpNode.makeLLVM();
                expInfo2 = innerRelExpNode.expInfo;
            }
            expInfo.setReg(llvmGenerator.makeLogicCalculateStmt(innerToken, expInfo, expInfo2));
            expInfo.varType = VarType.Int;
            innerToken = innerEqExpNode.eqlOrNeqToken;
            innerEqExpNode = innerEqExpNode.shorterEqExpNode;
            innerRelExpNode = innerEqExpNode == null ? null : innerEqExpNode.relExpNode;
        }
    }

    boolean calculateConstExp() throws ExpNotConstException {
        int ans = relExpNode.calculateConstExp();
        RelExpNode innerRelExpNode;
        EqExpNode innerEqExpNode = shorterEqExpNode;
        Token token = eqlOrNeqToken;
        innerRelExpNode = innerEqExpNode == null ? null : innerEqExpNode.relExpNode;
        while (innerRelExpNode != null) {
            switch (token.getType()) {
                case EQL:
                    ans = ans == innerRelExpNode.calculateConstExp() ? 1 : 0;
                    break;
                case NEQ:
                    ans = ans != innerRelExpNode.calculateConstExp() ? 1 : 0;
                    break;
                default:
                    break;
            }
            token = innerEqExpNode.eqlOrNeqToken;
            innerEqExpNode = innerEqExpNode.shorterEqExpNode;
            innerRelExpNode = innerEqExpNode == null ? null : innerEqExpNode.relExpNode;
        }
        return ans == 0 ? false : true;
    }

    @Override
    public String toString() {
        return "<EqExp>";
    }

    private EqExpNode() {}
}
