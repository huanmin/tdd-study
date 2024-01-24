package geektime.tdd.args.other;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

public class Args<T> {

    private final Class<T> optionClass;

    private final Map<Class<?>, OptionParser<?>> parsers;

    public Args(Class<T> optionClass, Map<Class<?>, OptionParser<?>> parsers) {
        this.optionClass = optionClass;
        this.parsers = parsers;
    }

    public static Map<String, String[]> toMap(String... arguments) {
        Map<String, String[]> result = new HashMap<>();

        String option = null;
        List<String> list = new ArrayList<>();
        for (String argument : arguments) {
            if (argument.matches("^-[a-zA-Z-]+$")) {
                if (option != null) result.put(option, list.toArray(String[]::new));
                option = argument.substring(1);
                list.clear();
            } else
                list.add(argument);
        }
        result.put(option, list.toArray(String[]::new));
        return result;
    }

    public T parse(String... args) {
        try {
            Map<String, String[]> options = toMap(args);

            Constructor<?> constructor = optionClass.getDeclaredConstructors()[0];

            Object[] value = Arrays.stream(constructor.getParameters()).map(it -> parseOption(it, options)).toArray();

            return (T) constructor.newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseOption(Parameter parameter, Map<String, String[]> options) {
        Option option = parameter.getAnnotation(Option.class);
        return parsers.get(parameter.getType()).parse(options.get(option.value()));
    }
}