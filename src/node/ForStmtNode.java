package node;

import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class ForStmtNode {//finish
    // ForStmt → LVal '=' Exp

    LValNode lValNode;
    Token assignToken;
    ExpNode expNode;

    public static ForStmtNode ForStmt() {
        Parser instance = Parser.getInstance();
        ForStmtNode forStmtNode = new ForStmtNode();
        LValNode lValNode;
        Token assignToken;
        ExpNode expNode;
        lValNode = LValNode.LVal();
        if(lValNode == null) {
            return null;
        }
        forStmtNode.lValNode = lValNode;
        assignToken = instance.peekNextToken();
        if(assignToken.getType().equals(TokenType.ASSIGN) == false) {
            return null;
        }
        forStmtNode.assignToken = assignToken;
        expNode = ExpNode.Exp();
        if(expNode == null) {
            return null;
        }
        forStmtNode.expNode = expNode;
        return forStmtNode;
    }

    void print() {
        lValNode.print();
        assignToken.print();
        expNode.print();
        System.out.println(toString());
    }

    void makeLLVM() {
        ExpInfo expNodeExpInfo, lValNodeExpInfo;
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        lValNode.makeLLVM();
        lValNodeExpInfo = lValNode.expInfo;
        if (lValNodeExpInfo.isArray == true) {
            lValNodeExpInfo.setReg(llvmGenerator.makeGetelementptrStmt(lValNodeExpInfo, lValNode.arrayNode.expNode.expInfo));
        }
        lValNode.checkIfConst();
        expNode.makeLLVM();
        expNodeExpInfo = expNode.expInfo;
        llvmGenerator.makeStoreStmt(expNodeExpInfo, lValNodeExpInfo);
    }

    @Override
    public String toString() {
        return "<ForStmt>";
    }

    private ForStmtNode() {}
}
