package geektime.tdd.args.exception;

import geektime.tdd.args.Option;

public class TooManyArgumentsException extends RuntimeException {
    private final Option option;

    public TooManyArgumentsException(Option option) {
        this.option = option;
    }

    public Option getOption() {
        return option;
    }
}
