import config.Config;
import frontend.Lexer;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws Exception {
        Config.setOriginalStream();
        lexerTest();
    }

    private static boolean lexerTest() throws Exception{
        Lexer instance = Lexer.getInstace();
        String rootPath = "test_lexer";
        String sourcePath = "/testfile.txt";
        String lexerPath = "/lexer.txt";
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
                    instance.lexerAnalyse(source);
                    System.setOut(new PrintStream(testCasePath + lexerPath));
                    instance.printTokens();
                    Scanner scLexer = new Scanner(new FileReader(testCasePath + lexerPath));
                    Scanner scAns = new Scanner(new FileReader(testCasePath + ansPath));
                    System.setOut(new PrintStream(testCasePath + resPath));
                    // System.setOut(Config.originalStream);
                    boolean success = true;
                    while(scLexer.hasNextLine() && scAns.hasNextLine()) {
                        String strLexer = scLexer.nextLine();
                        String strAns = scAns.nextLine();
                        if(strLexer.equals(strAns) == false) {
                            System.out.println(strLexer + "|" + strAns);
                            success = false;
                        }
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
    };
}
