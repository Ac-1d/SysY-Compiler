package node;

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

    private AddExpNode() {}
}
