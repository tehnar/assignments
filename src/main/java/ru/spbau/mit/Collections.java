package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seva on 29.09.15.
 */
public abstract class Collections {
    public static <T, R> Iterable<R> map (Function1<? super T, R> f, Iterable<T> array) {
        List<R> result = new ArrayList<>();
        for (T element : array) {
            result.add(f.apply(element));
        }
        return result;
    }

    public static <T> Iterable<T> filter(Predicate<? super T> p, Iterable<T> array) {
        List<T> result = new ArrayList<>();
        for (T element : array) {
            if (p.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    private static <T> Iterable<T> takeWhileBool(Predicate<? super T> p, Iterable<T> array, boolean statement) {
        List <T> result = new ArrayList<>();
        for (T element : array) {
            if (p.apply(element) != statement) {
                break;
            }
            result.add(element);
        }
        return result;
    }
    public static <T> Iterable<T> takeWhile(Predicate<? super T> p, Iterable<T> array) {
        return takeWhileBool(p, array, true);
    }

    public static <T> Iterable<T> takeUnless(Predicate<? super T> p, Iterable<T> array) {
        return takeWhileBool(p, array, false);
    }

    public static <T, E> E foldl(Function2<? super E, ? super T, ? extends E> f, E start, Iterable<T> array) {
        for (T element : array) {
            start = f.apply(start, element);
        }
        return start;
    }

    public static <T, E> E foldr(Function2<? super T, ? super E, ? extends E> f, E start, Iterable<T> array) {
        List <T> tmpArray = new ArrayList<>();
        for (T element : array) {
            tmpArray.add(element);
        }

        for (int i = tmpArray.size() - 1; i >= 0; i--) {
            start = f.apply(tmpArray.get(i), start);
        }
        return start;
    }
}
