package token;

public class Token {
    private TokenType type;
    private String word;
    private int lineNum;
    public Token(TokenType type, String word) {
        this.type = type;
        this.word = word;
    }
    public Token(TokenType type, String word, int lineNum) {
        this.type = type;
        this.word = word;
        this.lineNum = lineNum;
    }
    public TokenType getType() {
        return type;
    }
    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return type + " " + word;
    }

    public void print() {
        System.out.println(this.toString());
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return this.lineNum;
    }
}
