import config.Config;
import frontend.Lexer;
import frontend.Parser;
import frontend.SymbolHandler;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws Exception {
        Lexer lexer = Lexer.getInstace();
        Config.init();
        Config.lexer();
        String source;
        source = Files.readString(Paths.get(Config.fileInPath));
        boolean success = !lexer.lexerAnalyse(source + "\n");
        if(success) {
            lexer.printTokens();
        }
        else {
            lexer.printTokens();
            Config.error();
            lexer.printErrors();
        }
        Parser parse = Parser.getInstance();
        parse.parseAnalyse();
        if(parse.errorsList.isEmpty() == true) {
            parse.print();
        }
        else {
            Config.error();
            parse.printError();
        }
        System.setOut(Config.originalStream);
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        symbolHandler.analyse();
        if(symbolHandler.getErrorsList().isEmpty()) {
            symbolHandler.print();
        } else {
            Config.error();
            symbolHandler.printError();
        }
    }
}
