import config.Config;
import frontend.Lexer;
import frontend.Parse;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws Exception {
        Lexer lexer = Lexer.getInstace();
        Config.init();
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
        Parse parse = Parse.getInstance();
        parse.parseAnalyse();
    }
}
