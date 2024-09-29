package frontend;

import java.util.ArrayList;
import token.Token;

public class Parse {
    private static final Parse instance = new Parse();
    private final ArrayList<Token> tokensList = new ArrayList<>();
    private int index;
    public static Parse getInstance() {
        return instance;
    }

    public void init() {
        this.index = 0;
    }

    public void ignoreNextToken() {
        index++;
    }
    
    public Token peekNextToken() {
        Token token;
        try {
            token = tokensList.get(index);            
        } catch (Exception e) {
            token = null;
        }
        return token;
    }

    public Token getNextToken() {
        Token token = peekNextToken();
        this.index++;
        return token;
    }
    
}
