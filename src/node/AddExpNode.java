package node;

import Symbol.ExpInfo;
import Symbol.LLVMToken.LLVMToken;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class AddExpNode {//finish
    // AddExp → MulExp | AddExp ('+' | '−') MulExp 
    /*  change it to AddExp → MulExp | MulExp ('+' | '−')  AddExp 
     *  and rechange in print()
    */
    
    AddExpNode shorterAddExpNode;
    Token addToken;
    MulExpNode mulExpNode;

    public static AddExpNode AddExp() {
        Parser instance = Parser.getInstance();
        AddExpNode addExpNode = new AddExpNode();
        MulExpNode mulExpNode;
        Token addToken;
        AddExpNode shorterAddExpNode;
        int tmpIndex;
        tmpIndex = instance.getPeekIndex();
        mulExpNode = MulExpNode.MulExp();
        if(mulExpNode == null) {
            instance.setPeekIndex(tmpIndex);
            return null;
        }
        addExpNode.mulExpNode = mulExpNode;
        tmpIndex = instance.getPeekIndex();
        addToken = instance.peekNextToken();
        if(addToken.getType().equals(TokenType.PLUS) == false && addToken.getType().equals(TokenType.MINU) == false) {
            instance.setPeekIndex(tmpIndex);
            return addExpNode;
        }
        addExpNode.addToken = addToken;
        shorterAddExpNode = AddExpNode.AddExp();
        addExpNode.shorterAddExpNode = shorterAddExpNode;

        return addExpNode;
    }

    void print() {
        mulExpNode.print();
        System.out.println(toString());
        if(shorterAddExpNode != null) {
            addToken.print();
            shorterAddExpNode.print();
        }
    }

    ExpInfo makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo, expInfo2;
        MulExpNode innerMulExpNode;
        AddExpNode innerAddExpNode = shorterAddExpNode;
        Token innerAddToken = addToken;
        expInfo = mulExpNode.makeLLVM();
        innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        while (innerMulExpNode != null) {
            expInfo2 = innerMulExpNode.makeLLVM();
            expInfo.regIndex = llvmGenerator.makeCalculate(innerAddToken, new LLVMToken(expInfo), new LLVMToken(expInfo2));
            innerAddToken = innerAddExpNode == null ? null : innerAddExpNode.addToken;
            innerAddExpNode = innerAddExpNode.shorterAddExpNode;
            innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        }
        return expInfo;
    }

    public String toString() {
        return "<AddExp>";
    }

    private AddExpNode() {}
}
