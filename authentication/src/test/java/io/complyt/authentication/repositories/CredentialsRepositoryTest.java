package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Credentials;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class CredentialsRepositoryTest {
    @InjectMocks
    CredentialsRepository credentialsRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Credentials credentials;

    @BeforeEach
    void setup() {
        credentials = TestUtilities.createCredentials();
    }

    @Test
    void findByComplytClientId_credentialsWithComplytClientIdExists_returnsCredentials() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findOne(query, Credentials.class)).thenReturn(Mono.just(credentials));

        // Then
        Mono<Credentials> credentialsMono = credentialsRepository.findByComplytClientId(complytClientId);
        StepVerifier.create(credentialsMono).expectNext(credentials).verifyComplete();
    }

    @Test
    void findByComplytClientId_credentialsWithComplytClientIdNotExists_returnsMonoEmpty() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findOne(query, Credentials.class)).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsMono = credentialsRepository.findByComplytClientId(complytClientId);
        StepVerifier.create(credentialsMono).verifyComplete();
    }

    @Test
    void save_validCredentials_returnSavedCredentials() {
        // When
        when(reactiveMongoTemplate.save(credentials)).thenReturn(Mono.just(credentials));

        // Then
        Mono<Credentials> credentialsMono = credentialsRepository.save(credentials);
        StepVerifier.create(credentialsMono).expectNext(credentials).verifyComplete();
    }

    @Test
    void findByComplytId_complytClientIdIsNull_ThrowsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsRepository.findByComplytClientId(null);
        });

        assertEquals(nullPointerException.getMessage(), "complytClientId is marked non-null but is null");
    }

    @Test
    void findByComplytId_credentialsIsNull_ThrowsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            credentialsRepository.save(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }
}