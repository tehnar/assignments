package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.*;


public class Injector {
    private static HashSet<Class<?>> visitedClasses;
    private static ArrayList<Object> instatinatedClasses;
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
        instatinatedClasses = new ArrayList<>();
        return construct(rootConstructor, implementationClassNames);
    }

    private static Object construct(Constructor<?> rootConstructor, List<String> implementationClassNames) throws Exception {
        Class<?>[] parameterTypes = rootConstructor.getParameterTypes();
        ArrayList<Object> parameterInstances = new ArrayList<>();
        for (Class<?> param : parameterTypes) {
            for (Class <?> visitedClass : visitedClasses) {
                if (visitedClass.isAssignableFrom(param) || param.isAssignableFrom(visitedClass)) {
                    throw new InjectionCycleException();
                }
            }
            visitedClasses.add(param);
            Object paramInstance = null;
            for (Object instance : instatinatedClasses) {
                if (param.isInstance(instance)) {
                    paramInstance = instance;
                    break;
                }
            }
            if (paramInstance == null) {
                paramInstance = construct(getClassConstructor(param, implementationClassNames), implementationClassNames);
            }
            parameterInstances.add(paramInstance);
            instatinatedClasses.add(paramInstance);
            visitedClasses.remove(param);
        }

        return rootConstructor.newInstance(parameterInstances.toArray());
    }
}