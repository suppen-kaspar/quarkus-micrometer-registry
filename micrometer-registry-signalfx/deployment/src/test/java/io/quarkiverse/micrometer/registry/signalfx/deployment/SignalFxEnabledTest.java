package io.quarkiverse.micrometer.registry.signalfx.deployment;

import java.util.Set;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.quarkus.test.QuarkusUnitTest;

public class SignalFxEnabledTest {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.signalfx.SignalFxMeterRegistry";

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.export.signalfx.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.signalfx.access-token", "required")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Inject
    MeterRegistry registry;

    @Test
    public void testMeterRegistryPresent() {
        // SignalFx is enabled (alone, all others disabled)
        Assertions.assertNotNull(registry, "A registry should be configured");
        Set<MeterRegistry> subRegistries = ((CompositeMeterRegistry) registry).getRegistries();
        Assertions.assertEquals(1, subRegistries.size(),
                "There should be a sub-registry: " + subRegistries);
        Assertions.assertEquals(REGISTRY_CLASS_NAME, subRegistries.iterator().next().getClass().getName(),
                "Should be SignalFxMeterRegistry");
    }
}
