package geektime.tdd.args.mocks;

import java.util.Arrays;

public class Args<T> {


    private final OptionClass<T> optionClass;
    private final OptionParser parser;
    private final ValueRetriever retriever;

    public Args(OptionClass<T> optionClass, OptionParser parser, ValueRetriever retriever) {

        this.optionClass = optionClass;
        this.parser = parser;
        this.retriever = retriever;
    }


    public T parse(String... args) {
        return optionClass.create(Arrays.stream(optionClass.getOptionNames())
                .map(name -> parser.parse(optionClass.getOptionType(name), retriever.getValues(name, args)))
                .toArray(Object[]::new));
    }
}
