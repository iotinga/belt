package io.tinga.b3.agent.shadowing.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class VolatileVersionSafeExecutorTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);

    @InjectMocks
    VolatileVersionSafeExecutor sut;

    @Test
    void bindSetsCurrentReportedVersion() {
        sut.bind(topicBase, roleName);
        assertThat(sut.isInitialized()).isTrue();
        sut.safeExecute(versionSupplier -> {
            Integer version = versionSupplier.apply(false);
            assertThat(version).isEqualTo(1);
            return null;
        });
    }
    @Test
    void bindDoesntInitializeTwice() {
        sut.bind(topicBase, roleName);
        sut.safeExecute(versionSupplier -> {
            versionSupplier.apply(true);
            return null;
        });
        sut.bind(topicBase, roleName);
        sut.safeExecute(versionSupplier -> {
            Integer version = versionSupplier.apply(false);
            assertThat(version).isEqualTo(2);
            return null;
        });
    }

    @Test
    void shouldInitializeVersionCorrectly() {
        boolean result = sut.initCurrentReportedVersion(1);

        assertThat(result).isTrue();
        assertThat(sut.isInitialized()).isTrue();
    }

    @Test
    void shouldNotInitializeVersionIfAlreadyInitialized() {
        sut.initCurrentReportedVersion(1);
        boolean result = sut.initCurrentReportedVersion(2);

        assertThat(result).isFalse();
    }

    @Test
    void shouldExecuteCriticalSectionWithCurrentVersion() {
        sut.initCurrentReportedVersion(5);

        sut.safeExecute(versionSupplier -> {
            Integer version = versionSupplier.apply(false);
            assertThat(version).isEqualTo(5);
            return null;
        });
    }

    @Test
    void shouldExecuteCriticalSectionWithNextVersion() {
        sut.initCurrentReportedVersion(5);

        sut.safeExecute(versionSupplier -> {
            Integer version = versionSupplier.apply(true);
            assertThat(version).isEqualTo(6);
            return null;
        });

        sut.safeExecute(versionSupplier -> {
            Integer version = versionSupplier.apply(false);
            assertThat(version).isEqualTo(6);
            return null;
        });
    }

    @Test
    void shouldThrowExceptionIfSafeExecuteCalledBeforeInitialization() {
        assertThatThrownBy(() -> sut.safeExecute(versionSupplier -> {
            versionSupplier.apply(false);
            return null;
        })).isInstanceOf(InitializationException.class)
                .hasMessageContaining("currentReportedVersion is null");
    }
}
