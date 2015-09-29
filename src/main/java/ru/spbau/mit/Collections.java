package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seva on 29.09.15.
 */
public abstract class Collections {
    public static <T, R> Iterable<R> map (Function1<T, R> f, Iterable<T> array) {
        List<R> result = new ArrayList<>();
        for (T element : array) {
            result.add(f.apply(element));
        }
        return result;
    }

    public static <T> Iterable<T> filter(Predicate<T> p, Iterable<T> array) {
        List<T> result = new ArrayList<>();
        for (T element : array) {
            if (p.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    private static <T> Iterable<T> takeWhileBool(Predicate<T> p, Iterable<T> array, boolean statement) {
        List <T> result = new ArrayList<>();
        for (T element : array) {
            if (p.apply(element) != statement) {
                break;
            }
            result.add(element);
        }
        return result;
    }
    public static <T> Iterable<T> takeWhile(Predicate<T> p, Iterable<T> array) {
        return takeWhileBool(p, array, true);
    }

    public static <T> Iterable<T> takeUnless(Predicate<T> p, Iterable<T> array) {
        return takeWhileBool(p, array, false);
    }

    public static <T> T foldl(Function2<T, T, ? extends T> f, T start, Iterable<T> array) {
        for (T element : array) {
            start = f.apply(start, element);
        }
        return start;
    }

    public static <T> T foldr(Function2<T, T, ? extends T> f, T start, Iterable<T> array) {
        List <T> reversedArray = new ArrayList<>();
        for (T element : array) {
            reversedArray.add(element);
        }

        for (T element : reversedArray) {
            start = f.apply(start, element);
        }
        return start;
    }
}
