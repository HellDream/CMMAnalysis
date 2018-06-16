package exceptions;

/**
 * Created by Yu on 2017/12/2.
 */
public class CMMException extends Exception {
    private Error error;
    private int lineNum;
    private int location;
    public CMMException(Error error,String message) {
        super(message);
        this.error = error;
    }

    public CMMException( Error error,String message, int lineNum, int location) {
        super(message);
        this.error = error;
        this.lineNum = lineNum;
        this.location = location;
    }

    public Error getError() {
        return error;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getLocation() {
        return location;
    }
}
