package geektime.tdd.args;

import geektime.tdd.args.exception.IllegalOptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {

//    确定风格
//    @Test
//    public void test() {
//        Arguments args = Args.parse("l:b,p:d,d:s", "-l", "-p", "8080", "-d", "/usr/local");
//        args.getBool("l");
//        args.getInt("p");
//
//        Options options = Args.parse(Options.class, "-l", "-p", "8080", "-d", "/usr/local");
//        options.logging();
//        options.port();
//    }

    // multi option -l -p 8080 -d /usr/local
    @Test
    public void should_parse_multi_options() {
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/local");
        assertTrue(options.logging());
        assertEquals(8080, options.port());
        assertEquals("/usr/local", options.directory());
    }

    static record MultiOptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory){}


    @Test
    public void should_throw_illegal_exception_if_annotation_not_present() {
        IllegalOptionException e = assertThrows(IllegalOptionException.class, () ->
                Args.parse(OptionWithoutAnnotation.class, "-l", "-p", "8080", "-d", "/usr/local"));

        assertEquals("port", e.getParameter());
    }

    static record OptionWithoutAnnotation(@Option("l") boolean logging, int port, @Option("d") String directory){}

}
