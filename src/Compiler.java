import config.Config;
import frontend.Lexer;
import frontend.Parser;
import frontend.SymbolHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
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
        SymbolHandler symbolHandler = SymbolHandler.getInstance();
        symbolHandler.analyse();
        llvm();
    }

    private static void llvm() {
        String[] inputFiles = {"llvm_ir_data.txt", "llvm_ir_text.txt"};
        String outputFile = "llvm_ir.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String inputFile : inputFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.newLine();
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error reading file " + inputFile + ": " + e.getMessage());
                }
            }
            System.out.println("Files have been merged successfully into " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing to file " + outputFile + ": " + e.getMessage());
        }
    }
}
