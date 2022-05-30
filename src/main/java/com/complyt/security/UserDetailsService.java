package com.complyt.security;

import com.complyt.domain.security.Authority;
import com.complyt.repositories.security.AuthorityRepository;
import com.complyt.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {
    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final AuthorityRepository authorityRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByName(username)
                .flatMap(user -> Flux.fromIterable(user.getAuthorityIds())
                        .flatMap(authorityRepository::findById)
                        .collectList()
                        .map(authorities -> new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                user.getEnabled(),
                                user.getAccountNonExpired(),
                                user.getCredentialsNonExpired(),
                                user.getAccountNonLocked(),
                                convertToSpringAuthorities(authorities))));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(List<Authority> authorities) {
        if (authorities != null && authorities.size() > 0) {
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
        } else {
            return new HashSet<>();
        }
    }
}