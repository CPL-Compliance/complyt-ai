package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Token;
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
import test_utils.unit_tests.TestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class TokenRepositoryTest {
    @InjectMocks
    TokenRepository tokenRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Token token;

    @BeforeEach
    void setUp() {
        token = TestUtilities.createToken();
    }

    @Test
    public void findByComplytClientId_tokenExists_returnsToken() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findOne(query, Token.class)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> tokenMono = tokenRepository.findByComplytClientId(complytClientId);
        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }

    @Test
    public void findByComplytClientId_tokenNotExists_returnEmpty() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findOne(query, Token.class)).thenReturn(Mono.empty());

        // Then
        Mono<Token> tokenMono = tokenRepository.findByComplytClientId(complytClientId);
        StepVerifier.create(tokenMono).verifyComplete();
    }

    @Test
    public void save() {
        // When
        when(reactiveMongoTemplate.save(token)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> tokenMono = tokenRepository.save(token);
        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }
}