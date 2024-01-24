package geektime.tdd.args.other;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArgsMapTest {

    @Test
    public void should_parse_empty_value() {
        Map<String, String[]> args = Args.toMap("-l");
        assertEquals(1, args.size());
        assertArrayEquals(new String[]{}, args.get("l"));
    }

    @Test
    public void should_parse_single_value() {
        Map<String, String[]> args = Args.toMap("-p", "8080");
        assertEquals(1, args.size());
        assertArrayEquals(new String[]{"8080"}, args.get("p"));
    }

    @Test
    public void should_parse_multi_value() {
        Map<String, String[]> args = Args.toMap("-g", "this", "is");
        assertEquals(1, args.size());
        assertArrayEquals(new String[]{"this", "is"}, args.get("g"));
    }

    @Test
    public void should_parse_compose_single_value() {
        Map<String, String[]> args = Args.toMap("-l", "-p", "8080", "-d", "/var/log");
        assertEquals(3, args.size());
        assertArrayEquals(new String[]{}, args.get("l"));
        assertArrayEquals(new String[]{"8080"}, args.get("p"));
        assertArrayEquals(new String[]{"/var/log"}, args.get("d"));
    }

    @Test
    public void should_parse_compose_multi_value() {
        Map<String, String[]> args = Args.toMap("-g", "this", "is", "-d", "1", "2", "-3", "-5");
        assertEquals(2, args.size());
        assertArrayEquals(new String[]{"this", "is"}, args.get("g"));
        assertArrayEquals(new String[]{"1", "2", "-3", "-5"}, args.get("d"));
    }
}
