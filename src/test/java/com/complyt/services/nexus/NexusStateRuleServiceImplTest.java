package com.complyt.services.nexus;

import com.complyt.domain.CustomerType;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.repositories.NexusStateRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class NexusStateRuleServiceImplTest {
    @InjectMocks
    private NexusStateRuleServiceImpl nexusStateRuleServiceImpl;

    @Mock
    private NexusStateRuleRepository nexusStateRuleRepository;

    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA","02","California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000,2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(),true,state,taxableCategories,tangibleCategories,customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR,nexusThreshold);
    }

    @Test
    void save_SaveStateRule_Returns_StateRule() {
        // Given
        NexusStateRule nexusStateRuleNoId = nexusStateRule.withId(null);

        // When
        when(nexusStateRuleRepository.save(nexusStateRuleNoId)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleServiceImpl.save(nexusStateRuleNoId);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findOneByName() {
    }

    @Test
    void findByName() {
    }

    @Test
    void findById_FindsStateRule_ReturnsStateRule() {
        // Given
        String id = nexusStateRule.getId();

        // When
        when(nexusStateRuleRepository.findById(id)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleServiceImpl.findById(id);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusStateRuleServiceImpl.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findAll_FindsTwoRules_ReturnsTwoRules() {
        // Given
        State secondState = new State("NY","04","New-York");
        NexusStateRule secondRule = nexusStateRule
                .withId(UUID.randomUUID().toString())
                .withState(secondState);
        List<NexusStateRule> nexusStateRuleList = new ArrayList<NexusStateRule>() {{
            add(nexusStateRule);
            add(secondRule);
        }};

        // When
        when(nexusStateRuleRepository.findAll()).thenReturn(Flux.fromIterable(nexusStateRuleList));
        Flux<NexusStateRule> nexusStateRuleFlux = nexusStateRuleServiceImpl.findAll();

        // Then
        StepVerifier.create(nexusStateRuleFlux).expectNext(nexusStateRule).expectNext(secondRule).verifyComplete();
    }

    @Test
    void findByState_FindsRule_ReturnsRule() {
        when(nexusStateRuleRepository.findByState(nexusStateRule.getState().getAbbreviation())).thenReturn(Mono.just(nexusStateRule));

        Mono<NexusStateRule> result = nexusStateRuleServiceImpl.findByState(nexusStateRule.getState().getAbbreviation());

        StepVerifier.create(result).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findByState_NullStatePassed_ThrowsException() {
        // Given
        String nullState = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusStateRuleServiceImpl.findByState(nullState);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

}