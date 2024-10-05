package node;

import frontend.Parse;
import token.Token;
import token.TokenType;

public class CharacterNode {
    // Character → CharConst

    Token charConstToken;

    public static CharacterNode Character() {
        Parse instance = Parse.getInstance();
        CharacterNode characterNode = new CharacterNode();
        Token charConstToken;
        charConstToken = instance.peekNextToken();
        if(charConstToken.getType().equals(TokenType.CHRCON) == false) {
            return null;
        }
        characterNode.charConstToken = charConstToken;
        return characterNode;
    }
}
