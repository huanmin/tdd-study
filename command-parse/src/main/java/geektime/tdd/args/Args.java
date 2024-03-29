package geektime.tdd.args;

import geektime.tdd.args.exception.IllegalOptionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Args<T> {

    private Map<Class<?>, OptionParser> parsers;
    private Class<T> optionsClass;

    public Args(Map<Class<?>, OptionParser> parsers, Class<T> optionsClass) {
        this.parsers = parsers;
        this.optionsClass = optionsClass;
    }

    public static <T> T parse(Class<T> optionsClass, String... args) {
        return new Args<>(PARSERS, optionsClass).parse(args);
    }

    private T parse(String[] args) {
        try {
            List<String> arguments = Arrays.asList(args);

            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];

            Object[] value = Arrays.stream(constructor.getParameters()).map(it -> parseOption(it, arguments)).toArray();

            return (T) constructor.newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseOption(Parameter parameter, List<String> arguments) {
        if (!parameter.isAnnotationPresent(Option.class))
            throw new IllegalOptionException(parameter.getName());
        return parsers.get(parameter.getType()).parse(arguments, parameter.getAnnotation(Option.class));
    }

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, OptionParsers.bool(),
            int.class, OptionParsers.unary(Integer::parseInt, 8080),
            String.class, OptionParsers.unary(String::valueOf, "/var/logs"),
            String[].class, OptionParsers.list(String[]::new, String::valueOf),
            Integer[].class, OptionParsers.list(Integer[]::new, Integer::valueOf)
    );

}

