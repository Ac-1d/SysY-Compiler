package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class RelExpNode {//finish
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

    AddExpNode addExpNode;
    Token token;
    RelExpNode shorterRelExpNode;

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

    void setupSymbolTable() {
        addExpNode.makeLLVM();
        if (shorterRelExpNode != null) {
            shorterRelExpNode.setupSymbolTable();
        }
    }

    @Override
    public String toString() {
        return "<RelExp>";
    }

    private RelExpNode() {}
}
