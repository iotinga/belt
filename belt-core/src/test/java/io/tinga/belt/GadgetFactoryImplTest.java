package io.tinga.belt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.tinga.belt.config.PropertiesProvider;
import io.tinga.belt.dummy.DummyGadget;
import io.tinga.belt.output.GadgetDisplayFactory;
import io.tinga.belt.output.Status;

@ExtendWith(MockitoExtension.class)
class GadgetFactoryImplTest {

    private Faker faker;
    private GadgetContextFactoryImpl instance;
    private GadgetContextFactory testee;
    private GadgetDisplayFactory displayFactory;
    private PropertiesProvider propertiesProvider;

    private Injector injector;

    @BeforeEach
    public void setUpEach() {
        this.faker = new Faker();
        this.propertiesProvider = mock(PropertiesProvider.class);
        this.displayFactory = mock(GadgetDisplayFactory.class);
        this.injector = Guice.createInjector();
        this.instance = new GadgetContextFactoryImpl(this.injector, this.propertiesProvider, this.displayFactory);
        this.testee = this.instance;
    }

    @Test
    public void itCorrectlyBuildsValidPluginEntrypoint() throws GadgetLifecycleException {
        Callable<Status> result = this.testee.buildCallableFrom(DummyGadget.class.getCanonicalName());
        assertNotNull(result);
    }

    @Test
    public void itThrowsPluginLifecycleExceptionOnInvalidClass() {
        assertThrows(GadgetLifecycleException.class, () -> {
            this.testee.buildCallableFrom(faker.lorem().word());
        });
    }
}
