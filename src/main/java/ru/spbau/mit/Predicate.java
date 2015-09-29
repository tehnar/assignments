package ru.spbau.mit;

/**
 * Created by seva on 25.09.15.
 */
public abstract class Predicate <T> extends Function1 <T, Boolean> {
    public abstract Boolean apply(T x);

    public static final Predicate <Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            return true;
        }
    };

    public static final Predicate <Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean apply(Object x) {
            return false;
        }
    };

    public Predicate<T> or (final Predicate <? super T> predicate) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T x) {
                return Predicate.this.apply(x) || predicate.apply(x);
            }
        };
    }

    public Predicate <T> and (final Predicate <? super T> predicate) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T x) {
                return Predicate.this.apply(x) && predicate.apply(x);
            }
        };
    }

    public Predicate <T> not () {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T x) {
                return !Predicate.this.apply(x);
            }
        };
    }


}
