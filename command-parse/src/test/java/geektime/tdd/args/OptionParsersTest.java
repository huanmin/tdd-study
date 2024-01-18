package geektime.tdd.args;

import geektime.tdd.args.exception.IllegalValueException;
import geektime.tdd.args.exception.InsufficientArgumentsException;
import geektime.tdd.args.exception.TooManyArgumentsException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class OptionParsersTest {

    @Nested
    class UnaryOptionParser {
        // said path
        // - integer -p / -p 8080 8081
        // - string -d / -d /usr/ /var/
        // default value
        // -int 8080
        // -string /var/log
        @Test
        public void should_not_accept_extra_argument_for_single_value_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () ->
                    OptionParsers.unary(Integer::parseInt, 8080).parse(List.of("-p", "8080", "8081"), option("p")));
            assertEquals("p", e.getOption().value());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-p -l", "-p"})
        public void should_not_accept_Insufficient_argument_for_single_value_option(String arguments) {
            InsufficientArgumentsException e = assertThrows(InsufficientArgumentsException.class, () ->
                    OptionParsers.unary(Integer::parseInt, 8080).parse(List.of(arguments.split(" ")), option("p")));
            assertEquals("p", e.getOption().value());
        }

        @Test
        public void should_set_default_value_8080_for_int_option() {
            Function<String, Object> whatever = it -> null;
            Object defalutValue = new Object();
            assertSame(defalutValue, OptionParsers.unary(whatever, defalutValue).parse(List.of(), option("p")));
        }

        @Test
        public void should_parse_value_if_flag_present() {
            Object parsed = new Object();
            Function<String, Object> whatever = it -> parsed;
            assertSame(parsed, OptionParsers.unary(whatever, new Object()).parse(List.of("-p", "8081"), option("p")));
        }
    }

    @Nested
    class BooleanOptionParser {

        //single option
        // - bool -l
        // said path
        // - bool -l t / -l b 2
        // default value
        // -bool false
        @Test
        public void should_not_accept_extra_argument_for_boolean_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () ->
                    OptionParsers.bool().parse(Arrays.asList("-l", "t"), option("l")));

            assertEquals("l", e.getOption().value());
        }

        @Test
        public void should_set_default_value_to_false() {
            assertFalse(OptionParsers.bool().parse(List.of(), option("l")));
        }

        @Test
        public void should_set_boolean_option_to_true_if_flag_present() {
            assertTrue(OptionParsers.bool().parse(List.of("-l"), option("l")));
        }

        @Test
        @Disabled
        public void should_not_accept_extra_arguments_for_boolean_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () ->
                    OptionParsers.bool().parse(Arrays.asList("-l", "t", "s"), option("l")));

            assertEquals("l", e.getOption().value());
        }

    }

    @Nested
    class ListOptionParser {
        //-g this is a list
        @Test
        public void should_parse_list_value() {
            assertArrayEquals(new String[]{"this", "is"}, OptionParsers.list(String[]::new, String::valueOf).parse(List.of("-g", "this", "is"), option("g")));
        }

        @Test
        public void should_parse_negative() {
            assertArrayEquals(new String[]{"-1", "-2"}, OptionParsers.list(String[]::new, String::valueOf).parse(List.of("-g", "-1", "-2"), option("g")));
        }

        @Test
        public void should_use_empty_array_as_default_value() {
            String[] value = OptionParsers.list(String[]::new, String::valueOf).parse(List.of(), option("g"));
            assertEquals(0, value.length);
        }

        @Test
        public void should_throw_exception_if_value_cant_by_parse() {
            Function<String, String> parse = it -> {
                throw new RuntimeException();
            };
            IllegalValueException g = assertThrows(IllegalValueException.class, () -> OptionParsers.list(String[]::new, parse).parse(List.of("-g", "this", "is"), option("g")));
            assertEquals("g", g.getOption());
            assertEquals("this", g.getValue());
        }

        //-g this is a list -d 1 2 -1 4
        @Test
        public void should_example_2() {
            ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "4");
            assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.group());
            assertArrayEquals(new Integer[]{1, 2, -3, 4}, options.decimals());
        }

        static record ListOptions(@Option("g") String[] group, @Option("d") Integer[] decimals){}
    }

    static Option option(String value) {
        return new Option(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return Option.class;
            }

            @Override
            public String value() {
                return value;
            }
        };
    }
}
