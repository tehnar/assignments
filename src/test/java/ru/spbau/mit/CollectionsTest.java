package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by seva on 29.09.15.
 */
public class CollectionsTest {
    @Test
    public void testMap() {
        Function1<Object, Boolean> isOneCharacter = new Function1<Object, Boolean>() {
            @Override
            public Boolean apply(Object x) {
                return x.toString().length() == 1;
            }
        };
        List<Integer> arg1 = Arrays.asList(9, 10, 100);
        List<Boolean> result1 = Arrays.asList(true, false, false);
        assertTrue(result1.equals(Collections.map(isOneCharacter, arg1)));


    }

    @Test
    public void testFilter() {
        Predicate<Integer> isEven = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer x) {
                return x % 2 == 0;
            }
        };
        List<Integer> arg1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> result1 = Arrays.asList(2, 4, 6);
        assertTrue(result1.equals(Collections.filter(isEven, arg1)));
    }

    @Test
    public void testTakeWhile(){
        Predicate<Object> startsWithZero = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                return x.toString().startsWith("0");
            }
        };

        List<String> arg1 = Arrays.asList("0", "0123", "239", "017");
        List<String> result1 = Arrays.asList("0", "0123");
        assertTrue(result1.equals(Collections.takeWhile(startsWithZero, arg1)));

        List<Object> arg2 = new ArrayList<Object> (Arrays.asList("0", "0123", "017"));
        arg2.add(0);
        assertTrue(arg2.equals(Collections.takeWhile(startsWithZero, arg2)));
    }

    @Test
    public void testTakeUnless(){
        Predicate<Object> startsNotWithZero = new Predicate<Object>() {
            @Override
            public Boolean apply(Object x) {
                return !x.toString().startsWith("0");
            }
        };

        List<String> arg1 = Arrays.asList("0", "0123", "239", "017");
        List<String> result1 = Arrays.asList("0", "0123");
        assertTrue(result1.equals(Collections.takeUnless(startsNotWithZero, arg1)));
    }

    @Test
    public void testFoldl(){
        Function2<Object, Integer, String> f = new Function2<Object, Integer, String>() {
            @Override
            public String apply(Object x, Integer y) {
                return x.toString() + y.toString();
            }
        };
        List<Integer> test1 = Arrays.asList(1, 2, 3);
        assertTrue(Collections.foldl(f, (Object) "9", test1).equals("9123"));
    }

    @Test
    public void testFoldr(){
        Function2<Integer, Object, String> f = new Function2<Integer, Object, String>() {
            @Override
            public String apply(Integer x, Object y) {
                return x.toString() + y.toString();
            }
        };
        List<Integer> test1 = Arrays.asList(1, 2, 3);
        assertTrue(Collections.foldr(f, (Object) "9", test1).equals("1239"));
    }
}
