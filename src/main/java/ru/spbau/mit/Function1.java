package ru.spbau.mit;

/**
 * Created by seva on 23.09.15.
 */
public abstract class Function1 <T, R> {
    public abstract R apply(T x);

    public <E> Function1<T, E> compose(final Function1<? super R, E> g) {
        return new Function1<T, E>() {
            @Override
            public E apply(T x) {
                return g.apply(Function1.this.apply(x));
            }
        };
    }

    public static void main(String[] args) {
        Function1<Integer, Integer> fact = null;
        Function1<Object, String> g = null;

        fact.compose(g);

    }
}
