package io.tinga.belt.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import io.tinga.belt.GadgetFatalException;
import io.tinga.belt.input.GadgetCommandFactory;
import io.tinga.belt.testgadget.TestGadget;
import io.tinga.belt.testgadget.TestGadgetCommand;
import io.tinga.belt.testgadget.TestGadgetCommandOption;
import io.tinga.belt.testgadget.TestGadgetComposition;

class CliCommandFactoryTest {

    private Faker faker;
    private CliCommandFactory instance;
    private GadgetCommandFactory testee;

    @BeforeEach
    public void setUp() {
        this.faker = new Faker();
        this.instance = new CliCommandFactory();
        this.testee = this.instance;
    }

    @Test
    public void itThrowsWithZeroOnHelpRequestTest() {
        GadgetFatalException exception = assertThrows(GadgetFatalException.class, () -> {
            String[] args = {"-h"};
            this.testee.parseArgs(new TestGadget(), args);
        });
        assertEquals(0, exception.exitCode);
    }

    @Test
    public void itDoesntThrowsWithAtLeastOneModuleTest() throws GadgetFatalException {
        String[] args = {this.faker.lorem().word()};
        TestGadgetCommand command = this.testee.parseArgs(new TestGadget(), args);
        assertEquals(args[0], command.plugins().get(0));
    }

    @Test
    public void itSetDefaultsOnMinimalRequestTest() throws GadgetFatalException {
        String[] args = {this.faker.lorem().word()};
        TestGadgetCommand command = this.testee.parseArgs(new TestGadget(), args);
        assertEquals(args[0], command.plugins().get(0));
        assertEquals(Boolean.parseBoolean(TestGadgetCommandOption.IGNORE.defaultValue()), command.ignore());
        assertEquals(TestGadgetCommandOption.NAME.defaultValue(), command.name());
        assertEquals(TestGadgetComposition.valueOf(TestGadgetCommandOption.THREADING.defaultValue()), command.threading());
    }

    @Test
    public void itKeepsTheSpecifiedModulesOrder() throws GadgetFatalException {
        String[] args = {this.faker.lorem().word(), this.faker.lorem().word(), this.faker.lorem().word()};
        TestGadgetCommand command = this.testee.parseArgs(new TestGadget(), args);
        assertEquals(args[0], command.plugins().get(0));
        assertEquals(args[1], command.plugins().get(1));
        assertEquals(args[2], command.plugins().get(2));
    }

    @Test
    public void itCorrectlySetRequestValuesTest() throws GadgetFatalException {
        String[] args = { "-t", TestGadgetComposition.SEQUENTIAL.name(), "-n", this.faker.lorem().word(), "-i", "true", this.faker.lorem().word()};
        TestGadgetCommand command = this.testee.parseArgs(new TestGadget(), args);
        assertEquals(TestGadgetComposition.SEQUENTIAL, command.threading());
        assertEquals(args[3], command.name());
        assertEquals(true, command.ignore());
    }

}
