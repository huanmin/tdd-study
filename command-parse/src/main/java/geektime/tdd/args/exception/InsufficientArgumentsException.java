package geektime.tdd.args.exception;

import geektime.tdd.args.Option;

public class InsufficientArgumentsException extends RuntimeException {

    private final Option option;

    public InsufficientArgumentsException(Option option) {
        this.option = option;
    }

    public Option getOption() {
        return option;
    }
}
