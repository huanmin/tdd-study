package geektime.tdd.args.mocks;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArgsTest {

    @Test
    public void should_parse_args_by_demo() {
        OptionClass<IntOption> optionClass = mock(OptionClass.class);
        OptionParser parser = mock(OptionParser.class);
        ValueRetriever valueRetriever = mock(ValueRetriever.class);

        when(optionClass.getOptionNames()).thenReturn(new String[]{"p"});
        when(optionClass.getOptionType(eq("p"))).thenReturn(int.class);
        when(valueRetriever.getValues(eq("p"), eq(new String[]{"-p", "8080"}))).thenReturn(new String[]{"8080"});
        when(parser.parse(eq(int.class), eq(new String[]{"8080"}))).thenReturn(8080);
        when(optionClass.create(eq(new Object[]{8080}))).thenReturn(new IntOption(8080));

        Args<IntOption> args = new Args<>(optionClass, parser, valueRetriever);
        IntOption option = args.parse("-p", "8080");

        assertEquals(8080, option.port);
    }

    static record IntOption(@Option("p") int port) {}

}
