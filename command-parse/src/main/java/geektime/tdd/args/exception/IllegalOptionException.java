package geektime.tdd.args.exception;

public class IllegalOptionException extends RuntimeException {
    private String Parameter;

    public IllegalOptionException(String Parameter) {
        this.Parameter = Parameter;
    }

    public String getParameter() {
        return Parameter;
    }
}
