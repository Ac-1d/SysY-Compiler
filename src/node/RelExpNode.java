package node;

import Exception.ExpNotConstException;
import Symbol.ExpInfo;
import frontend.LLVMGenerator;
import frontend.Parser;
import token.Token;
import token.TokenType;

public class RelExpNode {//finish
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    AddExpNode addExpNode;
    Token token;
    RelExpNode shorterRelExpNode;
    ExpInfo expInfo = new ExpInfo();

    public static RelExpNode RelExp() {
        Parser instance = Parser.getInstance();
        RelExpNode relExpNode = new RelExpNode();
        AddExpNode addExpNode;
        Token token;
        RelExpNode shorterRelExpNode;
        int tmpIndex;
        addExpNode = AddExpNode.AddExp();
        if(addExpNode == null) {
            return null;
        }
        relExpNode.addExpNode = addExpNode;
        tmpIndex = instance.getPeekIndex();
        token = instance.peekNextToken();
        if(token.getType().equals(TokenType.LSS) == false && token.getType().equals(TokenType.GRE) == false && token.getType().equals(TokenType.LEQ) == false && token.getType().equals(TokenType.GEQ) == false) {
            instance.setPeekIndex(tmpIndex);
            return relExpNode;
        }
        relExpNode.token = token;
        shorterRelExpNode = RelExpNode.RelExp();
        if(shorterRelExpNode == null) {
            return null;
        }
        relExpNode.shorterRelExpNode = shorterRelExpNode;
        return relExpNode;
    }

    void print() {
        addExpNode.print();
        System.out.println(toString());
        if(shorterRelExpNode != null) {
            token.print();
            shorterRelExpNode.print();
        }
    }

    void makeLLVM() {
        LLVMGenerator llvmGenerator = LLVMGenerator.getInstance();
        ExpInfo expInfo2 = new ExpInfo();
        RelExpNode innerRelExpNode = shorterRelExpNode;
        AddExpNode innerAddExpNode = innerRelExpNode == null ? null : innerRelExpNode.addExpNode;
        Token innerToken = token;
        try {
            expInfo.setValue(addExpNode.calculateConstExp());
        } catch (ExpNotConstException e) {
            addExpNode.makeLLVM();
            expInfo = addExpNode.expInfo;
        }
        while (innerAddExpNode != null) {
            try {
                expInfo2.setValue(innerAddExpNode.calculateConstExp());
            } catch (ExpNotConstException e) {
                innerAddExpNode.makeLLVM();
                expInfo2 = innerAddExpNode.expInfo;
            }
            expInfo.setReg(llvmGenerator.makeCalculateStmt(innerToken, expInfo, expInfo2));
            innerToken = innerRelExpNode.token;
            innerRelExpNode = innerRelExpNode.shorterRelExpNode;
            innerAddExpNode = innerRelExpNode == null ? null : innerRelExpNode.addExpNode;
        }
    }

    int calculateConstExp() throws ExpNotConstException {
        int ans = addExpNode.calculateConstExp();
        AddExpNode innerAddExpNode;
        RelExpNode innerRelExpNode = shorterRelExpNode;
        Token token = this.token;
        innerAddExpNode = innerRelExpNode == null ? null : innerRelExpNode.addExpNode;
        while (innerAddExpNode != null) {
            switch (token.getType()) {
                case LSS:
                    ans = ans < innerAddExpNode.calculateConstExp() ? 1 : 0;
                    break;
                case LEQ:
                    ans = ans <= innerAddExpNode.calculateConstExp() ? 1 : 0;
                    break;
                case GRE:
                    ans = ans > innerAddExpNode.calculateConstExp() ? 1 : 0;
                    break;
                case GEQ:
                    ans = ans >= innerAddExpNode.calculateConstExp() ? 1 : 0;
                    break;
                default:
                    break;
            }
            token = innerRelExpNode.token;
            innerRelExpNode = innerRelExpNode.shorterRelExpNode;
            innerAddExpNode = innerRelExpNode == null ? null : innerRelExpNode.addExpNode;
        }
        return ans;
    }

    @Override
    public String toString() {
        return "<RelExp>";
    }

    private RelExpNode() {}
}
