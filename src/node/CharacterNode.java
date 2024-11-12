package node;

import frontend.Parser;
import token.Token;
import token.TokenType;

public class CharacterNode {
    // Character → CharConst

    Token charConstToken;

    public static CharacterNode Character() {
        Parser instance = Parser.getInstance();
        CharacterNode characterNode = new CharacterNode();
        Token charConstToken;
        charConstToken = instance.peekNextToken();
        if(charConstToken.getType().equals(TokenType.CHRCON) == false) {
            return null;
        }
        characterNode.charConstToken = charConstToken;
        return characterNode;
    }

    void print() {
        charConstToken.print();
        System.out.println(this.toString());
    }

    int getValue() {
        return charConstToken.getWord().charAt(1);
    }

    public String toString() {
        return "<Character>";
    }

    private CharacterNode() {}
}
