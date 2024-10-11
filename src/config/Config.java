package config;

import java.io.IOException;
import java.io.PrintStream;

public class Config {
    /**
     * The path of files.
     */
    public static String fileInPath = "testfile.txt";
    public static String fileOutPath = "output.txt";
    public static String lexerOutPath = "lexer.txt";
    public static String parserOutPath = "parser.txt";
    public static String stdErrPath = "error.txt";
    public static String stdTestPath = "test.txt";
    /**
     * stages of compilation
     */
    public static String compileState;

    public static PrintStream originalStream = System.out;

    public static void setOriginalStream() {
        originalStream = System.out;
    }

    public static void init() throws IOException {
        System.setOut(new PrintStream(lexerOutPath));
    }
    public static void parser() {
        try {
            System.setOut(new PrintStream(parserOutPath));
        } catch (IOException e) {}
    }
    public static void error() throws IOException {
        System.setOut(new PrintStream(stdErrPath));
    }
    public static void test() throws IOException {
        System.setOut(new PrintStream(stdTestPath));
    }
}
