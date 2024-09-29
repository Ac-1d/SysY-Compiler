package frontend;

public class Error {
    private String stage;
    private int position;
    private char errorCode;
    Error(String stage, int position, char errorCode) {
        this.stage = stage;
        this.position = position;
        this.errorCode = errorCode;
    }
    @Override
    public String toString() {
        return position + " " + Character.toString(errorCode);
    }
}
