package com.complyt.security;

import com.complyt.domain.security.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@TestComponent
public class UserDetailsServiceMock implements ReactiveUserDetailsService {
    private final User user;

    public UserDetailsServiceMock() {
        user = User
                .builder()
                .username("user")
                .password("password")
                .clientId(new ObjectId("507f191e810c19729de860ea"))
                .build();
    }

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return Mono.just(user);
    }
}