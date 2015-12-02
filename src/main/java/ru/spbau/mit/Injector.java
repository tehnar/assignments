package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Injector {
    private static HashSet<Class<?>> visitedClasses;

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static Constructor<?> getClassConstructor(Class<?> c, List<String> implementationClassNames) throws Exception {
        Constructor<?> result = null;
        for (String className : implementationClassNames) {
            Class<?> tmp = Class.forName(className);
            if (c.isAssignableFrom(tmp)) {
                if (result != null) {
                    throw new AmbiguousImplementationException();
                }
                result = tmp.getConstructors()[0];
            }
        }
        if (result == null) {
            throw new ImplementationNotFoundException();
        }
        return result;
    }

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class<?> rootClass = Class.forName(rootClassName);
        Constructor<?> rootConstructor = rootClass.getConstructors()[0];
        visitedClasses = new HashSet<>();
        visitedClasses.add(rootClass);
        return construct(rootConstructor, implementationClassNames);
    }

    private static Object construct(Constructor<?> rootConstructor, List<String> implementationClassNames) throws Exception {
        Class<?>[] parameterTypes = rootConstructor.getParameterTypes();
        ArrayList<Object> parameterInstances = new ArrayList<>();
        for (Class<?> param : parameterTypes) {
            if (visitedClasses.contains(param)) {
                throw new InjectionCycleException();
            }
            visitedClasses.add(param);
            parameterInstances.add(construct(getClassConstructor(param, implementationClassNames), implementationClassNames));
            visitedClasses.remove(param);
        }
        return rootConstructor.newInstance(parameterInstances.toArray());
    }
}