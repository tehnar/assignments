package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by seva on 29.09.15.
 */
public class PredicateTest {
    @Test
    public void testAlwaysTrue() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(0));
    }

    @Test
    public void testAlwaysFalse() {
        assertFalse(Predicate.ALWAYS_FALSE.apply("123"));
    }

    @Test
    public void testOr() {
        Predicate<Integer> f = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x == 5;
            }
        };

        Predicate<Object> g = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                return x.toString().equals("239");
            }
        };

        Predicate <Integer> or = f.or(g);
        assertTrue(or.apply(5));
        assertFalse(or.apply(238));
        assertTrue(or.apply(239));
    }

    @Test(expected = IllegalStateException.class)
    public void testOrLazyEvaluation() {
        Predicate<Integer> f = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x == 5;
            }
        };

        Predicate<Object> isEvaluated = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                throw new IllegalStateException();
            }
        };

        Predicate<Integer> or = f.or(isEvaluated);
        assertTrue(or.apply(5));
        or.apply(238);
    }

    @Test
    public void testAnd() {
        Predicate<Integer> f = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x >= 0;
            }
        };
        Predicate<Integer> g = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x <= 0;
            }
        };

        Predicate<Integer> and1 = f.and(g);
        Predicate<Integer> and2 = f.and(Predicate.ALWAYS_TRUE);
        Predicate<Integer> and3 = f.and(Predicate.ALWAYS_FALSE);
        assertTrue(and1.apply(0));
        assertFalse(and1.apply(1));
        assertTrue(and2.apply(1));
        assertFalse(and2.apply(-1));
        assertFalse(and3.apply(239));
    }

    @Test(expected = IllegalStateException.class)
    public void testAndLazyEvaluation() {
        Predicate<Integer> f = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x == 5;
            }
        };

        Predicate<Object> isEvaluated = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                throw new IllegalStateException();
            }
        };

        Predicate<Integer> and = f.and(isEvaluated);
        assertFalse(and.apply(6));
        and.apply(5);
    }

    @Test
    public void testNot() {
        Predicate<Integer> f = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x == 0;
            }
        };

        Predicate<Integer> not = f.not();
        assertTrue(not.apply(1));
        assertFalse(not.apply(0));
    }
}
