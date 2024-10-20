package frontend;

import error.Error;
import java.util.List;

public class ErrorHandler {
    private static final ErrorHandler instance = new ErrorHandler();
    private ErrorHandler() {}
    public static ErrorHandler getInstance() {
        return instance;
    }

    private List<Error> errorsList;
    public static int loopNum = 0;

    public List<Error> getErrorsList() {
        return errorsList;
    }

    public void printError() {
        for (Error error : errorsList) {
            System.out.println(error.toString());
        }
    }

    void init() {
        Parser parser = Parser.getInstance();
        errorsList = parser.errorsList;
    }

    public void analyse() {
        init();
        
    }

    public void addError(Error error) {
        errorsList.add(error);
    }
}
