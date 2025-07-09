package io.tinga.belt.headless;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.tinga.belt.GadgetContextFactory;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;

@ExtendWith(MockitoExtension.class)
class RuntimeCommandExecutorTest {
        private Faker faker;
        private HeadlessCommandExecutor instance;
        private GadgetCommandExecutor testee;

        @Mock
        private ExecutorService executor;

        @Mock
        private GadgetContextFactory factory;

        @BeforeEach
        public void setUpEach() {
                this.faker = new Faker();
                this.executor = Mockito.spy(Executors.newSingleThreadExecutor());
                Injector injector = Guice.createInjector(binder -> {
                        binder.bind(GadgetContextFactory.class).toInstance(factory);
                        binder.bind(ExecutorService.class).toInstance(executor);
                        binder.bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class);
                });
                this.instance = injector.getInstance(HeadlessCommandExecutor.class);
                this.testee = this.instance;
        }

        // @Test
        // public void itSubmitsAllThePluginsOnConcurrentThreadingTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {

        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> (String)
        // invocation
        // .getArguments()[0]);
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // false,
        // Arrays.asList(modules));
        // this.testee.submit(command).join();
        // verify(factory, times(modules.length)).buildCallableFrom(anyString());
        // verify(executor, times(modules.length)).execute(any(Runnable.class));
        // }

        // @Test
        // public void itSubmitsAllThePluginsOnSequentialThreadingTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {

        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> (String)
        // invocation
        // .getArguments()[0]);
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.SEQUENTIAL,
        // false,
        // Arrays.asList(modules));
        // this.testee.submit(command).join();
        // verify(factory, times(modules.length)).buildCallableFrom(anyString());
        // verify(executor, times(modules.length)).execute(any(Runnable.class));
        // }

        // @Test
        // public void itSubmitsAllThePluginsOnExceptionAndIgnoreTrueTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {
        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> (String)
        // invocation
        // .getArguments()[0]);
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // true,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(modules[0]))
        // .thenThrow(new GadgetLifecycleException(new DummyGadgetCommandExecutor()));
        // this.testee.submit(command).join();
        // verify(factory, times(modules.length)).buildCallableFrom(anyString());
        // verify(executor, times(modules.length - 1)).execute(any(Runnable.class));
        // }

        // @Test
        // public void itContinuesOnExceptionAndIgnoreTrueTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // true,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> (String)
        // invocation
        // .getArguments()[0]);
        // this.testee.submit(command).join();
        // verify(executor, times(1)).awaitTermination(any(Long.class),
        // any(TimeUnit.class));
        // }

        // @Test
        // public void itBuildsOnlyOnePluginOnExceptionAndIgnoreFalseTest() throws
        // GadgetLifecycleException {
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // false,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(modules[0]))
        // .thenThrow(new GadgetLifecycleException(new DummyGadgetCommandExecutor()));
        // this.testee.submit(command).join();
        // verify(factory, times(1)).buildCallableFrom(anyString());
        // verify(executor, times(0)).execute(any(Runnable.class));
        // }

        // @Test
        // public void itHaltsOnClassNotFoundExceptionAndIgnoreFalseTest() throws
        // GadgetLifecycleException {
        // when(factory.buildCallableFrom(anyString())).thenReturn(() -> {
        // return Status.OK;
        // });
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // false,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(modules[0]))
        // .thenThrow(new GadgetLifecycleException(new DummyGadgetCommandExecutor()));
        // this.testee.submit(command).join();
        // verify(factory, times(1)).buildCallableFrom(anyString());
        // verify(executor, times(0)).execute(any(Runnable.class));
        // verify(executor, times(1)).shutdownNow();
        // }

        // @Test
        // public void
        // itBuildsOnlyOnePluginOnPluginNotFoundExceptionAndIgnoreFalseTest() throws
        // GadgetLifecycleException {
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // false,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(modules[0]))
        // .thenThrow(new GadgetLifecycleException(new DummyGadgetCommandExecutor()));
        // this.testee.submit(command).join();
        // verify(factory, times(1)).buildCallableFrom(anyString());
        // verify(executor, times(0)).execute(any(Runnable.class));
        // }

        // @Test
        // public void
        // itGracefullyStopsExecutionOnExecutionExceptionAndIgnoreFalseTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {
        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> {
        // throw new GadgetLifecycleException(new DummyGadgetCommandExecutor());
        // });
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // false,
        // Arrays.asList(modules));
        // this.testee.submit(command).join();
        // verify(executor, times(1)).shutdown();
        // }

        // @Test
        // public void itGracefullyStopsExecutionOnExecutionExceptionAndIgnoreTrueTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {
        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> {
        // throw new GadgetLifecycleException(new DummyGadgetCommandExecutor());
        // });
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // true,
        // Arrays.asList(modules));
        // this.testee.submit(command).join();
        // verify(executor, times(1)).shutdown();
        // }

        @Test
        public void itDoesntSubmitAnyPluginToAShutdownExecutorServiceTest() {
                when(executor.isShutdown()).thenReturn(true);
                String[] modules = { faker.lorem().word(), faker.lorem().word(), faker.lorem().word() };
                HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
                                HeadlessAction.CONCURRENT,
                                false,
                                Arrays.asList(modules));
                this.testee.submit(command).join();
                verify(executor, times(0)).execute(any());
        }

        // @Test
        // public void itGracefullyShutdownsTest()
        // throws GadgetLifecycleException, InterruptedException, ExecutionException {
        // String[] modules = { faker.lorem().word(), faker.lorem().word(),
        // faker.lorem().word() };
        // HeadlessCommand command = new HeadlessCommand(faker.lorem().word(),
        // HeadlessGadgetComposition.CONCURRENT,
        // true,
        // Arrays.asList(modules));
        // when(factory.buildCallableFrom(anyString()))
        // .thenAnswer((Answer<Callable<String>>) invocation -> () -> (String)
        // invocation
        // .getArguments()[0]);
        // when(executor.isShutdown()).thenReturn(false);
        // when(executor.isTerminated()).thenReturn(true);
        // this.testee.submit(command).join();
        // verify(executor, times(1)).shutdown();
        // verify(executor, times(1)).awaitTermination(any(Long.class),
        // any(TimeUnit.class));
        // }
}
