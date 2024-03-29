package geektime.tdd.di;

import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class ContainerTest {


    ContextConfig config;

    @BeforeEach
    public void setUp() {
        config = new ContextConfig();
    }

    @Nested
    public class ComponentConstruction {
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component() {
            };
            config.bind(Component.class, instance);

            assertSame(instance, config.getContext().get(Component.class).get());
        }

        @Test
        public void should_return_empty_if_component_not_defined() {
            Optional<Component> component = config.getContext().get(Component.class);
            assertTrue(component.isEmpty());
        }

        //todo: instance
        // 抽象类
        // 接口

        // 构造函数注入
        @Nested
        public class ConstructorInjection {
            //happy path
            // 无依赖的组件应该通过默认构造函数生成组件实例
            // 有依赖的组件，通过 Inject 标注的构造函数生成组件实例
            // 如果所依赖的组件也存在依赖，那么需要对所依赖的组件也完成依赖注入
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                config.bind(Component.class, ComponentsWithDefaultConstructor.class);
                Component instance = config.getContext().get(Component.class).get();

                assertNotNull(instance);
                assertTrue(instance instanceof ComponentsWithDefaultConstructor);
            }

            @Test
            public void should_bind_type_to_a_class_with_injection_constructor() {
                Dependency dependency = new Dependency() {
                };
                config.bind(Dependency.class, dependency);
                config.bind(Component.class, ComponentWithInjectionConstructor.class);

                Component instance = config.getContext().get(Component.class).get();

                assertNotNull(instance);
                assertSame(dependency, ((ComponentWithInjectionConstructor) instance).getDependency());
            }

            //todo a -> b -> c
            @Test
            public void should_bind_type_to_a_class_with_transitive_dependency() {
                config.bind(Dependency.class, DependencyWithInjectionConstructor.class);
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(String.class, "indirect dependency");

                Component instance = config.getContext().get(Component.class).get();

                assertNotNull(instance);

                assertSame("indirect dependency",
                        ((DependencyWithInjectionConstructor)
                                ((ComponentWithInjectionConstructor) instance).getDependency())
                                .getDependency());
            }

            //said path
            // 如果组件有多于一个 Inject 标注的构造函数，则抛出异常
            // 如果组件需要的依赖不存在，则抛出异常
            // 如果组件间存在循环依赖，则抛出异常
            @Test
            public void should_throw_exception_when_component_has_multiple_injection_constructor() {
                assertThrows(IllegalComponentException.class, () -> {
                    config.bind(Component.class, ComponentWithMultiInjectionConstructor.class);
                });
            }

            @Test
            public void should_throw_exception_when_component_has_no_injection_constructor() {
                assertThrows(IllegalComponentException.class, () ->
                    config.bind(Component.class, ComponentWithNoInjectionConstructor.class));
            }

            @Test
            public void should_throw_exception_when_component_has_no_dependency() {
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.getContext());

                assertEquals(Dependency.class, exception.getDependency());
                assertEquals(Component.class, exception.getComponent());
            }

            @Test
            public void should_throw_exception_when_component_has_transitive_dependency_not_found() {
                config.bind(Dependency.class, DependencyWithInjectionConstructor.class);
                config.bind(Component.class, ComponentWithInjectionConstructor.class);

                DependencyNotFoundException exception = assertThrows(DependencyNotFoundException.class, () -> config.getContext());

                assertEquals(String.class, exception.getDependency());
                assertEquals(Dependency.class, exception.getComponent());
            }

            @Test
            public void should_throw_exception_when_component_has_circular_dependency() {
                config.bind(Dependency.class, ComponentWithCircularDependency.class);
                config.bind(Component.class, ComponentWithInjectionConstructor.class);

                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.getContext());
                assertEquals(2, exception.getComponent().toArray(Class<?>[]::new).length);
                assertTrue(exception.getComponent().containsAll(asList(Component.class, Dependency.class)));
            }

            @Test
            public void should_throw_exception_when_component_has_transitive_cyclic_dependencies() {
                config.bind(AnotherDependency.class, AnotherDependencyDependedOnComponent.class);
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(Dependency.class, DependencyDependedOnDependency.class);

                CyclicDependenciesFoundException exception = assertThrows(CyclicDependenciesFoundException.class, () -> config.getContext());

                assertEquals(3, exception.getComponent().toArray(Class<?>[]::new).length);
                assertTrue(exception.getComponent().containsAll(asList(Component.class, Dependency.class, AnotherDependency.class)));
            }
        }


        // 字段注入
        // 通过 Inject 标注将字段声明为依赖组件
        // 如果组件需要的依赖不存在，则抛出异常
        // 如果字段为 final 则抛出异常
        // 如果组件间存在循环依赖，则抛出异常
        @Nested
        public class FieldInjection {

        }

        // 方法注入
        // 通过 Inject 标注的方法，其参数为依赖组件
        // 通过 Inject 标注的无参数方法，会被调用
        // 按照子类中的规则，覆盖父类中的 Inject 方法
        // 如果组件需要的依赖不存在，则抛出异常
        // 如果方法定义类型参数，则抛出异常
        // 如果组件间存在循环依赖，则抛出异常

        @Nested
        public class MethodInjection {

        }
    }


    @Nested
    public class DependenciesSelection {

    }


    // 对于依赖选择部分，我分解的任务列表如下：
    // 对 Provider 类型的依赖
    // 注入构造函数中可以声明对于 Provider 的依赖
    // 注入字段中可以声明对于 Provider 的依赖
    // 注入方法中可声明对于 Provider 的依赖
    //
    // 自定义 Qualifier 的依赖
    // 注册组件时，可额外指定 Qualifier
    // 注册组件时，可从类对象上提取 Qualifier
    // 寻找依赖时，需同时满足类型与自定义 Qualifier 标注
    // 支持默认 Qualifier——Named

    @Nested
    public class LifecycleManagement {

    }

    // 对于生命周期管理部分，我分解的任务列表如下：
    // Singleton 生命周期
    // 注册组件时，可额外指定是否为 Singleton
    // 注册组件时，可从类对象上提取 Singleton 标注
    // 对于包含 Singleton 标注的组件，在容器范围内提供唯一实例
    // 容器组件默认不是 Single 生命周期

    // 自定义 Scope 标注
    // 可向容器注册自定义 Scope 标注的回调
}

interface Component {
}

interface Dependency {

}

interface AnotherDependency {

}

class ComponentsWithDefaultConstructor implements Component {
    public ComponentsWithDefaultConstructor() {
    }
}

class ComponentWithMultiInjectionConstructor implements Component {
    private String name;
    private Inject age;

    @Inject
    public ComponentWithMultiInjectionConstructor(String name, Inject age) {
        this.name = name;
        this.age = age;
    }

    @Inject
    public ComponentWithMultiInjectionConstructor(String name) {
        this.name = name;
    }
}

class ComponentWithNoInjectionConstructor implements Component {
    private String name;

    public ComponentWithNoInjectionConstructor(String name) {
        this.name = name;
    }
}

class ComponentWithInjectionConstructor implements Component {
    private Dependency dependency;

    @Inject
    public ComponentWithInjectionConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }
}

class DependencyWithInjectionConstructor implements Dependency {
    private String dependency;

    @Inject
    public DependencyWithInjectionConstructor(String dependency) {
        this.dependency = dependency;
    }

    public String getDependency() {
        return dependency;
    }
}

class ComponentWithCircularDependency implements Dependency {
    private Component component;

    @Inject
    public ComponentWithCircularDependency(Component component) {
        this.component = component;
    }
}

class AnotherDependencyDependedOnComponent implements AnotherDependency {
    private Component component;

    @Inject
    public AnotherDependencyDependedOnComponent(Component component) {
        this.component = component;
    }
}

class DependencyDependedOnDependency implements Dependency {
    private AnotherDependency anotherDependency;

    @Inject
    public DependencyDependedOnDependency(AnotherDependency anotherDependency) {
        this.anotherDependency = anotherDependency;
    }
}