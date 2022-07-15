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
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
                TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR,nexusThreshold);
    }

    @Test
    void save() {
    }

    @Test
    void findOneByName() {
    }

    @Test
    void findByName() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByState() {
        when(nexusStateRuleRepository.findByState(nexusStateRule.getState().getAbbreviation())).thenReturn(Mono.just(nexusStateRule));

        Mono<NexusStateRule> result = nexusStateRuleServiceImpl.findByState(nexusStateRule.getState().getAbbreviation());

        StepVerifier.create(result).expectNext(nexusStateRule).verifyComplete();
    }
}