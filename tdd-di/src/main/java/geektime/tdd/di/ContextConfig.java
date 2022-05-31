package geektime.tdd.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ContextConfig {

    private final Map<Class<?>, ComponentProvider<?>> providers = new HashMap<>();
    private final Map<Class<?>, List<Class<?>>> dependencies = new HashMap<>();

    public <Type> void bind(Class<Type> type, Type instance) {
        providers.put(type, context -> instance);
        dependencies.put(type, Collections.emptyList());
    }

    public <Type, Implementation extends Type>
    void bind(Class<Type> type, Class<Implementation> implementation) {
        Constructor<Implementation> injectionConstructor = getInjectConstructor(implementation);
        providers.put(type, new ConstructorInjectionProvider<>(type, injectionConstructor));
        dependencies.put(type, Arrays.stream(injectionConstructor.getParameterTypes()).toList());
    }

    public Context getContext() {
        for (Class<?> component : dependencies.keySet()) {
            for (Class<?> dependency : dependencies.get(component)) {
                if (!providers.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            }
        }
        return new Context() {
            @Override
            public <Type> Optional<Type> get(Class<Type> type) {
                return Optional.ofNullable(providers.get(type))
                        .map(provider -> (Type) provider.get(this));
            }
        };
    }

    interface ComponentProvider<T> {
        T get(Context context);
    }

    class ConstructorInjectionProvider<T> implements ComponentProvider<T> {
        private final Class<?> component;
        private final Constructor<T> injectionConstructor;
        private volatile boolean constructing;

        ConstructorInjectionProvider(Class<?> component, Constructor<T> injectionConstructor) {
            this.component = component;
            this.injectionConstructor = injectionConstructor;
            constructing = false;
        }

        @Override
        public T get(Context context) {
            if (constructing) throw new CircularDependencyException(component);
            try {
                constructing = true;
                Object[] dependencies = Arrays.stream(injectionConstructor.getParameters())
                        .map(p -> context.get(p.getType())
                                .orElseThrow(() -> new DependencyNotFoundException(p.getType(), component)))
                        .toArray(Object[]::new);
                return injectionConstructor.newInstance(dependencies);
            }catch (CircularDependencyException e) {
                throw new CircularDependencyException(component, e);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private <Type> Constructor<Type> getInjectConstructor(Class<Type> implementation) {
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
}
