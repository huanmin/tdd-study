package geektime.tdd.args.mocks;

public interface OptionParser {
    Object parse(Class<?> optionClass, String[] value);
}
