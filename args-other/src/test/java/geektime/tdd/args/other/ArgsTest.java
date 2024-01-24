package geektime.tdd.args.other;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgsTest {

    @Test
    public void should_parse_bool_value() {
        Args<BooleanOption> args = new Args<>(BooleanOption.class, Map.of(Boolean.class, ArgsTest::parseBool));

        BooleanOption option = args.parse("-l");
        assertTrue(option.logging);
    }

    static record BooleanOption(@Option("l") Boolean logging) {}

    private static boolean parseBool(String[] values) {
        return true;
    }
}
