package config;

import java.io.FileOutputStream;
import java.io.FileWriter;
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
    public static String symbolOutPath = "symbol.txt";
    public static String LLVMTextPath = "llvm_ir_text.txt";
    public static String LLVMConstStringPath = "llvm_ir_data.txt";
    public static String LLVMOutPath = "llvm_ir.txt";
    public static String stdErrPath = "error.txt";
    public static String stdTestPath = "test.txt";
    /**
     * stages of compilation
     */
    public static String compileState;

    public static PrintStream originalStream = System.out;

    private static void setOriginalStream() {
        originalStream = System.out;
    }

    public static void init() {
        Config.setOriginalStream();
    }

    public static void lexer() throws IOException {
        compileState = "lexer";
        System.setOut(new PrintStream(lexerOutPath));
    }
    public static void parser() {
        compileState = "parser";
        try {
            System.setOut(new PrintStream(parserOutPath));
        } catch (IOException e) {}
    }
    public static void symbol() {
        try {
            System.setOut(new PrintStream(symbolOutPath));
        } catch (Exception e) {}
    }
    public static void llvmText() {
        try {
            System.setOut(new PrintStream(LLVMTextPath));
        } catch (Exception e) {}
        try (FileWriter writer = new FileWriter(LLVMConstStringPath)) {} catch (Exception e) {}
    }
    public static void llvmData() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(LLVMConstStringPath, true)));
        } catch (Exception e) {}
    }
    public static void continueLLVMText() {
        try {
            System.setOut(new PrintStream(new FileOutputStream(LLVMTextPath, true)));
        } catch (Exception e) {}
    }
    public static void llvm() {
        try {
            System.setOut(new PrintStream(LLVMOutPath));
        } catch (Exception e) {}
    }
    public static void error() throws IOException {
        System.setOut(new PrintStream(stdErrPath));
    }
    public static void test() throws IOException {
        System.setOut(new PrintStream(stdTestPath));
    }
}
