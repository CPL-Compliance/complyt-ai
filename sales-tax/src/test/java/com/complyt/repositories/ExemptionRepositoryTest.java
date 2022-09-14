package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.*;
import com.complyt.domain.customer.exemption.ExemptionType;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfigMockTest.class)
public class ExemptionRepositoryTest {

    @InjectMocks
    ExemptionRepository exemptionRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    User user;
    Exemption exemption;
    Transaction transaction;
    ObjectId clientId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();
        exemption = createExemption();
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();

        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, exemption.getState().getAbbreviation(), "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, exemption.getCustomerId(), null, null, TransactionStatus.ACTIVE, exemption.getClientId(), null, externalTimeStamps);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), clientId, new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByClientCustomerAndState_FindsExemption_ReturnsExemption() {
        // Given
        Query query = Query.query(Criteria
                .where("clientId").is(user.getClientId())
                .and("customerId").is(transaction.getCustomerId())
                .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

        // When
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.just(exemption));
        Mono<Exemption> exemptionMono = exemptionRepository.findByClientCustomerAndState(transaction);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

}
