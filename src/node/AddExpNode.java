package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
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
    ExpInfo expInfo;
    MulExpNode mulExpNode;

    static AddExpNode AddExp() {
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

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2;
        MulExpNode innerMulExpNode;
        AddExpNode innerAddExpNode = shorterAddExpNode;
        Token innerAddToken = addToken;
        mulExpNode.makeLLVM();
        expInfo = mulExpNode.expInfo;
        innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        while (innerMulExpNode != null) {
            innerMulExpNode.makeLLVM();
            expInfo2 = innerMulExpNode.expInfo;
            expInfo.regIndex = llvmGenerator.makeCalculate(innerAddToken, true, expInfo.regIndex, true, expInfo2.regIndex);
            innerAddToken = innerAddExpNode == null ? null : innerAddExpNode.addToken;
            innerAddExpNode = innerAddExpNode.shorterAddExpNode;
            innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        }
    }

    int calculateConstExp() throws ExpNotConstException {
        int ans = mulExpNode.calculateConstExp();
        MulExpNode innerMulExpNode;
        AddExpNode innerAddExpNode = shorterAddExpNode;
        Token innerAddToken = addToken;
        innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        while (innerMulExpNode != null) {
            if (innerAddToken.getType().equals(TokenType.PLUS) == true) {
                ans += innerMulExpNode.calculateConstExp();
            } else {
                ans -= innerMulExpNode.calculateConstExp();
            }
            innerAddToken = innerAddExpNode == null ? null : innerAddExpNode.addToken;
            innerAddExpNode = innerAddExpNode.shorterAddExpNode;
            innerMulExpNode = innerAddExpNode == null ? null : innerAddExpNode.mulExpNode;
        }
        return ans;
    }

    public String toString() {
        return "<AddExp>";
    }

    private AddExpNode() {}
}
