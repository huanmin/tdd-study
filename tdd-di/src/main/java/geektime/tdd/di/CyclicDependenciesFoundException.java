package geektime.tdd.di;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class CyclicDependenciesFoundException extends RuntimeException {
    private Set<Class<?>> components = new HashSet<>();

    public CyclicDependenciesFoundException(Stack<Class<?>> visiting) {
        components.addAll(visiting);
    }

    public Set<Class<?>> getComponent() {
        return components;
    }
}
