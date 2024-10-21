import config.Config;
import frontend.ErrorHandler;
import frontend.Lexer;
import frontend.Parser;
import frontend.SymbolHandler;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws Exception {
        Config.init();
        lexerTest();
    }

    private static boolean lexerTest() throws Exception{
        Lexer lexer = Lexer.getInstace();
        Parser parser = Parser.getInstance();
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        String rootPath = "test_symbolHandler";
        String sourcePath = "/testfile.txt";
        String outPath = "/symbol.txt";
        String ansPath = "/ans.txt";
        String resPath = "/res.txt";
        for (DiffType type : DiffType.values()) {
            String path = rootPath + "/" + type.toString();
            File folder = new File(path);
            File[] testCaseFoldersList = folder.listFiles();
            for (File testCaseFolder : testCaseFoldersList) {
                String testCasePath = testCaseFolder.getPath();
                String source = Files.readString(Paths.get(testCasePath + sourcePath)) + "\n";
                try {
                    {
                        lexer.lexerAnalyse(source);
                        parser.parseAnalyse();
                        symbolHandler.analyse();
                        System.setOut(new PrintStream(testCasePath + outPath));
                        symbolHandler.print();
                    }
                    Scanner scLexer = new Scanner(new FileReader(testCasePath + outPath));
                    Scanner scAns = new Scanner(new FileReader(testCasePath + ansPath));
                    System.setOut(new PrintStream(testCasePath + resPath));
                    // System.setOut(Config.originalStream);
                    boolean success = true;
                    int i = 1;
                    while(scLexer.hasNextLine() && scAns.hasNextLine()) {
                        String strLexer = scLexer.nextLine();
                        String strAns = scAns.nextLine();
                        if(strLexer.equals(strAns) == false) {
                            System.out.println(strLexer + "|" + strAns + "|" + i);
                            success = false;
                        }
                        i++;
                    }
                    if(scLexer.hasNextLine() != scAns.hasNextLine()) {
                        System.setOut(Config.originalStream);
                        System.out.println(testCasePath + " ERROR");
                    }
                    else if(success) {
                        System.out.println("Success!");
                    }
                    scLexer.close();
                    scAns.close();
                } catch (Exception e) {
                    System.setOut(Config.originalStream);
                    System.out.println(testCasePath + e.toString());
                }

            }
        }
        return true;
    }

    enum DiffType {
        A,
        B,
        C,
        // ERROR,
    };
}
