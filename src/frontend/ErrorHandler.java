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

    private void init() {
        SymbolHandler instance = SymbolHandler.getInstance();
        errorsList = instance.getErrorsList();
    }

    public void analyse() {
        init();
        
    }

    public void addError(Error error) {
        errorsList.add(error);
    }
}
