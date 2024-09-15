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

public class UsaAddressItemsJurisdictionalRulesInjectorTest {

    UsaAddressExemptionListGenerator usaAddressExemptionListGenerator;

    UnitTestUtilities testUtilities;

    ExemptionWrapper exemptionWrapper;

    @BeforeEach
    void setUp() {
        List<State> states = UnitTestUtilities.createStateList();
        Exemption exemption = new UnitTestUtilities(LocalDateTime.now(), null).createExemption(UUID.randomUUID().toString());
        exemptionWrapper = new ExemptionWrapper(exemption, states);
        usaAddressExemptionListGenerator = new UsaAddressExemptionListGenerator();
    }

    @Test
    void build_OneState_ReturnsListOfOneExemption() {
        // Given
        List<Exemption> exemptions = new ArrayList<>() {{
            add(exemptionWrapper.exemption().withState(exemptionWrapper.states().get(0)));
        }};
        ExemptionWrapper wrapper = exemptionWrapper.withStates(new ArrayList<>() {{
            add(exemptionWrapper.states().get(0));
        }});

        // When
        Flux<Exemption> exemptionFlux = usaAddressExemptionListGenerator.generate(wrapper).apply(wrapper);

        // Then
        StepVerifier.create(exemptionFlux).expectNext(exemptions.get(0)).verifyComplete();
    }

    @Test
    void build_ThreeStates_ReturnsListOfThreeExemptions() {
        // Given

        // When
        Flux<Exemption> exemptionFlux = usaAddressExemptionListGenerator.generate(exemptionWrapper).apply(exemptionWrapper);

        // Then
        StepVerifier.create(exemptionFlux)
                .expectNext(exemptionWrapper.exemption().withState(exemptionWrapper.states().get(0)))
                .expectNext(exemptionWrapper.exemption().withState(exemptionWrapper.states().get(1)))
                .expectNext(exemptionWrapper.exemption().withState(exemptionWrapper.states().get(2)))
                .verifyComplete();
    }
}
