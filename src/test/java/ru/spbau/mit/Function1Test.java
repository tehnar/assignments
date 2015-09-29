package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by seva on 29.09.15.
 */
public class Function1Test {
    @Test
    public void testCompose() {
        Function1<Object, String> g = new Function1<Object, String>() {
            @Override
            public String apply(Object x) {
                return x.toString();
            }
        };

        Function1<Integer, Integer> f = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x * x;
            }
        };

        Function1<Integer, String> comp = f.compose(g);
        assertTrue(g.apply(-1).equals("-1"));
        assertTrue(f.apply(5).equals(25));
        assertTrue(comp.apply(5).equals("25"));
    }
}
