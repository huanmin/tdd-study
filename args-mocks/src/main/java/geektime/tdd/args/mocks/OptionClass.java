package geektime.tdd.args.mocks;

public interface OptionClass<T> {

    String[] getOptionNames();

    Class getOptionType(String name);

    T create(Object[] value);
}
