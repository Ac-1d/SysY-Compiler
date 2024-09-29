package frontend;

import config.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import error.Error;
import token.Token;
import token.TokenType;

public class Lexer {
    private static final Lexer instance = new Lexer();
    public static Lexer getInstace() {
        return instance;
    }

    private int lineNum = 1;
    private int curPos = 0;
    private int maxPos;
    private String source;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private boolean analyseActivate = true;
    private boolean errorState = false;
    private final ArrayList<Error> errors = new ArrayList<>();

    public void printTokens() {
        for (Token token : tokens) {
            System.out.println(token.toString());
        }
    }

    public void printErrors() {
        for (Error error : errors) {
            System.out.println(error.toString());
        }
    }
    //stolen from 🌸🍃
    private final Map<String, TokenType> reserveWords = new HashMap<String, TokenType>() {{
        put("main", TokenType.MAINTK);
        put("const", TokenType.CONSTTK);
        put("int", TokenType.INTTK);
        put("char", TokenType.CHARTK);
        put("void", TokenType.VOIDTK);
        put("break", TokenType.BREAKTK);
        put("continue", TokenType.CONTINUETK);
        put("if", TokenType.IFTK);
        put("else", TokenType.ELSETK);
        put("for", TokenType.FORTK);
        put("getint", TokenType.GETINTTK);
        put("getchar", TokenType.GETCHARTK);
        put("printf", TokenType.PRINTFTK);
        put("return", TokenType.RETURNTK);
    }};
    private final Map<String, TokenType> independentTokens = new HashMap<String, TokenType>() {{
        put("+", TokenType.PLUS);
        put("-", TokenType.MINU);
        put("*", TokenType.MULT);
        put("%", TokenType.MOD);
        put(";", TokenType.SEMICN);
        put(",", TokenType.COMMA);
        put("(", TokenType.LPARENT);
        put(")", TokenType.RPARENT);
        put("[", TokenType.LBRACK);
        put("]", TokenType.RBRACK);
        put("{", TokenType.LBRACE);
        put("}", TokenType.RBRACE);
    }};
    //coupleTokens有点少，后续如果需要可以进行拓展
    private final Map<String, TokenType> indOrCouTokens = new HashMap<String, TokenType>() {{
        put("<", TokenType.LSS);
        put(">", TokenType.GRE);
        put("=", TokenType.ASSIGN);
        put("!", TokenType.NOT);
    }};
    private final Map<String, TokenType> equalTokens = new HashMap<String, TokenType>() {{
        put("<", TokenType.LEQ);
        put(">", TokenType.GEQ);
        put("=", TokenType.EQL);
        put("!", TokenType.NEQ);
    }};

    private void init() {
        Config.compileState = "lexer";
        this.analyseActivate = true;
        this.maxPos = source.length();
        this.curPos = 0;
        this.lineNum = 1;
        this.tokens.clear();
        this.errors.clear();
    }

    public boolean lexerAnalyse(String source) {
        this.source = source;
        this.init();
        while(this.analyseActivate) {
            //getNextToken
            Token token = getNextToken();
            if(token != null) {
                tokens.add(token);
                // System.out.println(token.toString());
            }
            //错误处理
        }
        return this.errorState;
    }

    /**
     * 
     * @return true: 读到最后一个字符
     */
    private boolean ignoreNextChar() {
        if(peekNextChar() == '\n') {
            lineNum++;
        }
        curPos++;
        if(curPos >= maxPos) {
            analyseActivate = false;
        }
        return curPos >= maxPos;
    }

    private char peekNextChar() {
        return source.charAt(curPos);
    }

    private Token getNextToken() {
        Token token;
        StringBuilder word = new StringBuilder();
        TokenType type = null;
        int state = 0;
        //滤去空格与回车
        while(peekNextChar() == ' ' || peekNextChar() == '\n') {
            if(ignoreNextChar()) {
                return null;
            }
        }
        boolean finish = false;
        while(!finish && analyseActivate) {
            char curChar = peekNextChar();
            switch (state) {
                case 0://初始状态
                    if(isIdentifierNondigit(curChar)) {
                        state = 1;
                    }
                    else if(isNonZeroDigit(curChar)) {
                        state = 2;
                    }
                    else if(curChar == '0') {//数字0
                        type = TokenType.INTCON;
                        finish = true;
                    }
                    else if(curChar == '\"') {
                        state = 3;
                    }
                    else if(curChar == '\'') {
                        state = 5;
                    }
                    else if(isIndependentToken(curChar)) {//一些独立符号
                        finish = true;
                        for (Map.Entry<String, TokenType> independentToken : independentTokens.entrySet()) {
                            if(independentToken.getKey().equals(Character.toString(curChar))) {
                                type = independentToken.getValue();
                                break;
                            }
                        }
                        if(type == null) {
                            //错误输出
                        }
                    }
                    else if(curChar == '|') {
                        state = 9;
                    }
                    else if(curChar == '&') {
                        state = 10;
                    }
                    else if(isIndOrCouToken(curChar)) {
                        state = 11;
                    }
                    else if(curChar == '/') {
                        state = 13;
                    }
                    else {
                        state = 18;
                    }
                    ignoreNextChar();
                    word.append(curChar);
                    break;
                case 1://标识符 or 关键字
                    if(isIdentifierNondigit(curChar) || Character.isDigit(curChar)) {
                        word.append(curChar);
                        ignoreNextChar();
                    }
                    else {
                        finish = true;
                        //寻找关键字
                        for (Map.Entry<String, TokenType> reserveWord : reserveWords.entrySet()) {
                            if(reserveWord.getKey().equals(word.toString())) {
                                type = reserveWord.getValue();
                                break;
                            }
                        }
                        if(type == null) {
                            type = TokenType.IDENFR;
                        }
                    }
                    break;
                case 2://INT 常量
                    if(Character.isDigit(curChar)) {
                        word.append(curChar);
                        finish = ignoreNextChar();
                    }
                    else {
                        finish = true;
                        type = TokenType.INTCON;
                    }
                    break;
                case 3://STRING 常量
                    if(curChar == '\"') {
                        word.append(curChar);
                        finish = true;
                        type = TokenType.STRCON;
                        ignoreNextChar();
                    }
                    else {
                        word.append(curChar);
                        finish = ignoreNextChar();
                    }
                    break;
                case 5://CHAR 常量
                    if(curChar == '\'') {
                        word.append(curChar);
                        finish = true;
                        type = TokenType.CHRCON;
                        ignoreNextChar();
                    }
                    else if(curChar == '\\') {
                        word.append(curChar);
                        ignoreNextChar();
                        word.append(peekNextChar());
                        ignoreNextChar();
                    }
                    else {
                        word.append(curChar);
                        ignoreNextChar();
                    }
                    break;
                case 9:// ||
                    if(curChar == '|') {
                        word.append(curChar);
                        finish = true;
                        type = TokenType.OR;
                        ignoreNextChar();
                    }
                    else {
                        //错误处理
                        errors.add(new Error("LEX", lineNum, 'a'));
                        this.errorState = true;
                        return new Token(TokenType.OR, "||");
                    }
                    break;
                case 10:// &&
                    if(curChar == '&') {
                        word.append(curChar);
                        finish = true;
                        type = TokenType.AND;
                        ignoreNextChar();
                    }
                    else {
                        //错误处理
                        errors.add(new Error("LEX", lineNum, 'a'));
                        this.errorState = true;
                        return new Token(TokenType.AND, "&&");
                    }
                    break;
                case 11:// <>=!与=
                    if(curChar == '=') {
                        for(Map.Entry<String, TokenType> equalToken: equalTokens.entrySet()) {
                            if(word.toString().equals(equalToken.getKey())) {
                                word.append(curChar);
                                type = equalToken.getValue();
                                finish = true;
                                ignoreNextChar();
                            }
                        }
                        if(type == null) {
                            //错误处理
                            System.out.println("ERROR in case 11");
                        }
                    }
                    else {
                        for(Map.Entry<String, TokenType> indOrCouToken: indOrCouTokens.entrySet()) {
                            if(word.toString().equals(indOrCouToken.getKey())) {
                                type = indOrCouToken.getValue();
                                finish = true;
                            }
                        }
                        if(type == null) {
                            //错误处理
                            System.out.println("ERROR in case 11");
                        }
                    }
                    break;
                case 13:// 收到"/"
                    if(curChar == '/') {
                        state = 14;
                        ignoreNextChar();
                    }
                    else if(curChar == '*') {
                        state = 15;
                        ignoreNextChar();
                    }
                    else {
                        type = TokenType.DIV;
                        finish = true;
                    }
                    break;
                case 14:// 收到"//"
                    if(curChar == '\n') {
                        ignoreNextChar();
                        return null;
                    }
                    else {
                        finish = ignoreNextChar();
                    }
                    break;
                case 15:// 收到"/*"
                    if(curChar == '*') {
                        state = 16;
                        ignoreNextChar();
                    }
                    else{
                        ignoreNextChar();
                    }
                    break;
                case 16:// 收到"/*XX*"
                    if(curChar == '*') {
                        ignoreNextChar();
                    }
                    else if(curChar == '/') {
                        state = 17;
                        ignoreNextChar();
                    }
                    else {
                        state = 15;
                        ignoreNextChar();
                    }
                    break;
                case 17:// 收到"/*XX*/"
                    return null;
                case 18://错误处理
                    return null;
                default:
                    break;
            }
        }
        token = new Token(type, word.toString());
        return token;
    }

    private boolean isIdentifierNondigit(char c) {
        return c == '_' || Character.isLetter(c);
    }

    private boolean isIndependentToken(char c) {
        return c == '+' || c == '-' || c == '*' || c == '%' || c == ';' || c == ',' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}';
    }

    /**"<>=!" 四个既可独立出现又可以与'='相连的符号 */
    private boolean isIndOrCouToken(char c) {
        return c == '<' || c == '>' || c == '=' || c == '!';
    }

    private boolean isNonZeroDigit(char c) {
        return Character.isDigit(c) && c != '0';
    }

    public int getLineNum() {
        return lineNum;
    }
}
