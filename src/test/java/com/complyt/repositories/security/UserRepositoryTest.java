package com.complyt.repositories.security;

import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @InjectMocks
    UserRepository userRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    User user;

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .username("user")
                .password("password")
                .clientId(new ObjectId("507f191e810c19729de860ea"))
                .build();
    }

    @Test
    void findByName_FindsUser_ReturnsUser() {
        // Given
        String name = user.getUsername();
        Query query = Query.query(Criteria.where("username").is(name));

        // When
        when(reactiveMongoTemplate.findOne(query,User.class)).thenReturn(Mono.just(user));
        Mono<User> userMono = userRepository.findByName(name);

        // Then
        StepVerifier.create(userMono).expectNext(user).verifyComplete();

    }

    @Test
    void insert_UserInserted_UserReturned() {
        // Given

        // When
        when(reactiveMongoTemplate.save(user)).thenReturn(Mono.just(user));
        Mono<User> userMono = userRepository.insert(user);

        // Then
        StepVerifier.create(userMono).expectNext(user).verifyComplete();
    }
}