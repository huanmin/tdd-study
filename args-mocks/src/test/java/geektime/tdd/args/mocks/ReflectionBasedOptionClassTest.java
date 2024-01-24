package geektime.tdd.args.mocks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ReflectionBasedOptionClassTest {

    @Test
    public void should_treat_parameter_with_option_annotation_as_option() {
        OptionClass<IntOption> optionClass = new ReflectionBasedOptionClass<>(IntOption.class);
        assertArrayEquals(new String[]{"p"}, optionClass.getOptionNames());
    }

    static record IntOption(@Option("p") int port) {}
}
