package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfigMockTest.class)
public class SalesTaxTrackingRepositoryTest {

    @InjectMocks
    SalesTaxTrackingRepository salesTaxTrackingRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    SalesTaxTracking salesTaxTracking;
    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();
        salesTaxTracking = createSalesTaxTracking();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state,
                user.getClientId(), true, null,
                null,null,true, LocalDateTime.now());
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByState_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String stateAbbreviation = salesTaxTracking.getState().getAbbreviation();
        Query query = Query.query(Criteria.where("state.abbreviation").is(stateAbbreviation).and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingRepository.findByState(stateAbbreviation);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findByState_NullState_ThrowsException() {
        // Given
        String nullStateAbbreviation = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRepository.findByState(nullStateAbbreviation);
        });

        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void save_SavesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking salesTaxTrackingNoId = salesTaxTracking.withId(null);

        // When
        when(reactiveMongoTemplate.save(salesTaxTrackingNoId)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.save(salesTaxTrackingNoId);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void save_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingRepository.save(nullSalesTaxTracking));

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findById_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // given
        String id = salesTaxTracking.getId();
        Query query = Query.query(Criteria.where("_id").is(id).and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query,SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.findById(id);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findAll_FindsTwoSalesTaxTracking_ReturnsTwoSalesTaxTracking() {
        // given
        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<>() {{
            add(salesTaxTracking);
        }};
        Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.find(query,SalesTaxTracking.class)).thenReturn(Flux.fromIterable(salesTaxTrackingList));
        Flux<SalesTaxTracking> salesTaxTrackingFlux = salesTaxTrackingRepository.findAll();

        // Then
        StepVerifier.create(salesTaxTrackingFlux).expectNext(salesTaxTracking).verifyComplete();
    }
}