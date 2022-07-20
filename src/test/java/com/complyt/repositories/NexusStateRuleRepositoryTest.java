package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.CustomerType;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithUserDetails;
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
@Import(SecurityConfigMockTest.class)
public class NexusStateRuleRepositoryTest {

    @InjectMocks
    NexusStateRuleRepository nexusStateRuleRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    User user;
    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR, nexusThreshold);
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findById_FindsStateRule_ReturnsStateRule() {
        // Given
        String id = nexusStateRule.getId();

        // When
        when(reactiveMongoTemplate.findById(id, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findById(id);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusStateRuleRepository.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByState_FindsRule_ReturnsRule() {
        // Given
        String stateAbbreviation = nexusStateRule.getState().getAbbreviation();
        Query query = Query.query(Criteria.where("state.abbreviation").is(stateAbbreviation));

        // When
        when(reactiveMongoTemplate.findOne(query, NexusStateRule.class)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.findByState(stateAbbreviation);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void findByState_NullStatePassed_ThrowsException() {
        // Given
        String nullStateAbbreviation = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusStateRuleRepository.findByState(nullStateAbbreviation);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void save_SavesStateRule_ReturnsStateRule() {
        // Given

        // When
        when(reactiveMongoTemplate.save(nexusStateRule)).thenReturn(Mono.just(nexusStateRule));
        Mono<NexusStateRule> actualStateRule = nexusStateRuleRepository.save(nexusStateRule);

        // Then
        StepVerifier.create(actualStateRule).expectNext(nexusStateRule).verifyComplete();
    }

    @Test
    void save_NullStateRule_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusStateRuleRepository.save(nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findAll_FindsTwoStateRules_ReturnsTwoStateRules() {
        // Given
        State secondState = new State("NY", "04", "New-York");
        NexusStateRule secondStateRule = nexusStateRule.withState(secondState);
        List<NexusStateRule> nexusStateRules = new ArrayList<NexusStateRule>() {{
            add(nexusStateRule);
            add(secondStateRule);
        }};

        // When
        when(reactiveMongoTemplate.findAll(NexusStateRule.class)).thenReturn(Flux.fromIterable(nexusStateRules));
        Flux<NexusStateRule> nexusStateRuleFlux = nexusStateRuleRepository.findAll();

        // Then
        StepVerifier.create(nexusStateRuleFlux).expectNext(nexusStateRule).expectNext(secondStateRule).verifyComplete();
    }

}
