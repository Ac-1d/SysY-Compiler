package error;

public class Error implements Comparable<Error> {
    private final int position;
    private final ErrorType errorType;
    public Error(int position, ErrorType errorType) {
        this.position = position;
        this.errorType = errorType;
    }
    @Override
    public String toString() {
        return position + " " + errorType.toString();
    }
    
    @Override
    public int compareTo(Error o) {
        return this.position - o.position;
    }
}
