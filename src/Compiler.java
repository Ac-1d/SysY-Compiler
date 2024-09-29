import config.Config;
import frontend.Lexer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws Exception {
        Lexer instance = Lexer.getInstace();
        Config.init();
        String source;
        source = Files.readString(Paths.get(Config.fileInPath));
        boolean success = !instance.lexerAnalyse(source + "\n");
        if(success) {
            instance.printTokens();
        }
        else {
            instance.printTokens();
            Config.error();
            instance.printErrors();
        }
    }
}
