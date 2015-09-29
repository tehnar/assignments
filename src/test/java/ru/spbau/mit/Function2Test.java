package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by seva on 29.09.15.
 */
public class Function2Test {
    @Test
    public void testCompose() {
        Function2<Integer, Integer, String> f = new Function2<Integer, Integer, String> () {
            @Override
            public String apply(Integer x, Integer y) {
                return new Integer(x + y).toString();
            }
        };

        Function1<String, Integer> g = new Function1<String, Integer>() {
            @Override
            public Integer apply(String x) {
                return x.length();
            }
        };

        Function2<Integer, Integer, Integer> comp = f.compose(g);
        assertTrue(comp.apply(2, 7).equals(1));
        assertTrue(comp.apply(3, 7).equals(2));
        assertTrue(comp.apply(239, -240).equals(2));
    }

    @Test
    public void testBind1() {
        Function2 <Integer, Integer, Boolean> f = new Function2<Integer, Integer, Boolean>() {
            @Override
            public Boolean apply(Integer x, Integer y) {
                return x - y > 0;
            }
        };

        Function1<Integer, Boolean> g = f.bind1(5);
        assertTrue(g.apply(4));
        assertFalse(g.apply(5));
    }

    @Test
    public void testBind2() {
        Function2<Integer, Integer, Integer> f = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x - y;
            }
        };
        Function1<Integer, Integer> g = f.bind2(5);
        assertTrue(g.apply(5).equals(0));
        assertTrue(g.apply(3).equals(-2));
    }

    @Test
    public void testCurry() {
        Function2<Integer, String, Boolean> f = new Function2<Integer, String, Boolean>() {
            @Override
            public Boolean apply(Integer x, String y) {
                return x.toString().length() == y.length();
            }
        };
        Function1<Integer, Function1<String, Boolean>> g = f.curry();
        assertTrue(g.apply(5).apply("3"));
        assertFalse(g.apply(123).apply("0239"));
    }
}
