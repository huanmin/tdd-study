package geektime.tdd.args.other;

interface OptionParser<T> {
    T parse(String[] value);
}
