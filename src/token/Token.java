package token;

public class Token {
    private TokenType type;
    private String word;
    public Token(TokenType type, String word) {
        this.type = type;
        this.word = word;
    }
    public TokenType getType() {
        return type;
    }
    public String word() {
        return word;
    }

    @Override
    public String toString() {
        return type + " " + word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
