package frontend;

import java.io.IOException;
import java.io.PrintStream;

public class Config {
    /**
     * The path of files.
     */
    public static String fileInPath = "testfile.txt";
    public static String fileOutPath = "output.txt";
    public static String stdOutPath = "lexer.txt";
    public static String stdErrPath = "error.txt";
    public static String stdTestPath = "test.txt";
    /**
     * stages of compilation
     */
    public static boolean lexer = true;

    public static PrintStream originalStream;

    public static void setOriginalStream() {
        originalStream = System.out;
    }

    public static void init() throws IOException {
        System.setOut(new PrintStream(stdOutPath));
    }
    public static void error() throws IOException {
        System.setOut(new PrintStream(stdErrPath));
    }
    public static void test() throws IOException {
        System.setOut(new PrintStream(stdTestPath));
    }
}
