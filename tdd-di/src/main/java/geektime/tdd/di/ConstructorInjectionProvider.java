package geektime.tdd.di;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

class ConstructorInjectionProvider<T> implements ComponentProvider<T> {
    private final Constructor<T> injectionConstructor;

    public ConstructorInjectionProvider(Class<T> component) {
        this.injectionConstructor = getInjectConstructor(component);
    }

    private static <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {
        List<Constructor<?>> constructors = Arrays.stream(implementation.getConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class)).toList();

        if (constructors.size() > 1) throw new IllegalComponentException();

        return (Constructor<Type>) constructors.stream()
                .findFirst()
                .orElseGet(() -> {
                    try {
                        return implementation.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalComponentException();
                    }
                });
    }

    @Override
    public T get(Context context) {
        try {
            Object[] dependencies = Arrays.stream(injectionConstructor.getParameters())
                    .map(p -> context.get(p.getType()).get())
                    .toArray(Object[]::new);
            return injectionConstructor.newInstance(dependencies);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> getDenpendencies() {
        return Arrays.stream(injectionConstructor.getParameterTypes()).toList();
    }

}
