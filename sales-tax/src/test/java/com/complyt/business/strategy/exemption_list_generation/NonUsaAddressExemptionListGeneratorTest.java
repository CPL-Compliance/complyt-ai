package com.complyt.business.strategy.exemption_list_generation;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NonUsaAddressExemptionListGeneratorTest {

    NonUsaAddressExemptionListGenerator nonUsaAddressExemptionListGenerator;

    UnitTestUtilities testUtilities;

    ExemptionWrapper exemptionWrapper;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        List<State> states = testUtilities.createStateList();
        Exemption exemption = testUtilities.createExemption(UUID.randomUUID().toString());
        exemptionWrapper = testUtilities.createNonUsaExemptionWrapper("id");
        nonUsaAddressExemptionListGenerator = new NonUsaAddressExemptionListGenerator();
    }

    @Test
    void build_NullState_ReturnsListOfOneExemption() {
        // Given
        List<Exemption> exemptions = new ArrayList<>() {{
            add(exemptionWrapper.exemption().withState(null));
        }};

        // When
        Flux<Exemption> exemptionFlux = nonUsaAddressExemptionListGenerator.generate(exemptionWrapper).apply(exemptionWrapper);

        // Then
        StepVerifier.create(exemptionFlux).expectNext(exemptions.get(0)).verifyComplete();
    }

    @Test
    void build_ThreeStates_ReturnsListOfOneExemption() {
        // Given

        // When
        Flux<Exemption> exemptionFlux = nonUsaAddressExemptionListGenerator.generate(exemptionWrapper).apply(exemptionWrapper);

        // Then
        StepVerifier.create(exemptionFlux)
                .expectNext(exemptionWrapper.exemption().withState(null))
                .verifyComplete();
    }
}
