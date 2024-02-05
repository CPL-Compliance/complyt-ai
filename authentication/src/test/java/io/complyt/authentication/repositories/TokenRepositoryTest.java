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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void findByComplytClientId_tokenExists_returnsToken() {
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
    void findByComplytClientId_tokenNotExists_returnEmpty() {
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
    void save_tokenIsValid_returnsTokenFromDb() {
        // When
        when(reactiveMongoTemplate.save(token)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> tokenMono = tokenRepository.save(token);
        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }

    @Test
    void deleteByComplytClientId_complytClientIdExists_returnsTheDeletedToken() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findAndRemove(query, Token.class)).thenReturn(Mono.just(token));

        // Then
        Mono<Token> tokenMono = tokenRepository.deleteByComplytClientId(complytClientId);
        StepVerifier.create(tokenMono).expectNext(token).verifyComplete();
    }

    @Test
     void deleteByComplytClientId_tokenNotExists_returnsEmpty() {
        // Given
        String complytClientId = "complytClientId";
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        // When
        when(reactiveMongoTemplate.findAndRemove(query, Token.class)).thenReturn(Mono.empty());

        // Then
        Mono<Token> tokenMono = tokenRepository.deleteByComplytClientId(complytClientId);
        StepVerifier.create(tokenMono).verifyComplete();
    }

    @Test
    void findByComplytClientId_complytClientIdIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenRepository.findByComplytClientId(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "complytClientId is marked non-null but is null");
    }

    @Test
    void save_tokenIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenRepository.save(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "token is marked non-null but is null");
    }

    @Test
    void deleteByComplytClientId_tokenIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenRepository.deleteByComplytClientId(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "complytClientId is marked non-null but is null");
    }


}