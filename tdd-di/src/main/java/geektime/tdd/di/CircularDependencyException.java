package geektime.tdd.di;

import java.util.HashSet;
import java.util.Set;

public class CircularDependencyException extends RuntimeException {
    private Set<Class<?>> components = new HashSet<>();

    public CircularDependencyException(Class<?> component) {
        components.add(component);
    }

    public CircularDependencyException(Class<?> component, CircularDependencyException e) {
        components.add(component);
        components.addAll(e.components);
    }

    public Class<?>[] getComponents() {
        return components.toArray(Class<?>[]::new);
    }
}
