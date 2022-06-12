package com.complyt.security;

import com.complyt.domain.security.Authority;
import com.complyt.repositories.security.AuthorityRepository;
import com.complyt.repositories.security.RoleRepository;
import com.complyt.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@AllArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {
    @NonNull
    private UserRepository userRepository;

    @NonNull
    private AuthorityRepository authorityRepository;

    @NonNull
    private RoleRepository roleRepository;

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return userRepository.findByName(username)
                .flatMap(user -> Flux.fromIterable(user.getRoleIds())
                        .flatMap(roleRepository::findById)
                        .flatMapIterable(role -> role.getAuthorityIds())
                        .flatMap(authorityRepository::findById)
                        .collect(toList())
                        .map(this::convertToSpringAuthorities)
                        .map(user::withAuthorities));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(List<Authority> authorities) {
        if (authorities != null && authorities.size() > 0) {
            return authorities.stream()
                    .map(Authority::getPermission)
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
        } else {
            return new HashSet<>();
        }
    }
}