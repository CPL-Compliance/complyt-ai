package com.complyt.domain.security;


import com.complyt.repositories.security.AuthorityRepository;
import com.complyt.repositories.security.RoleRepository;
import com.complyt.repositories.security.UserRepository;
import com.complyt.security.UserDetailsService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @InjectMocks
    UserDetailsService userDetailsService;

    @Mock
    UserRepository userRepository;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    RoleRepository roleRepository;

    private String userName;
    private ObjectId firstAuthorityObjectId;
    private ObjectId secondAuthorityObjectId;
    private ObjectId firstRoleObjectId;
    private ObjectId secondRoleObjectId;
    private User user;
    private Role role1;
    private Role role2;
    private Authority authority1;
    private Authority authority2;

    @BeforeEach
    void setUp() {
        firstRoleObjectId = new ObjectId();
        secondRoleObjectId = new ObjectId();
        Set<ObjectId> roleIds = new HashSet<ObjectId>() {{
            add(firstRoleObjectId);
            add(secondRoleObjectId);
        }};

        firstAuthorityObjectId = new ObjectId();
        secondAuthorityObjectId = new ObjectId();
        Set<ObjectId> authorityIds = new HashSet<ObjectId>() {{
            add(firstAuthorityObjectId);
            add(secondAuthorityObjectId);
        }};

        role1 = new Role(firstRoleObjectId.toString(), "firstName", authorityIds);
        role2 = new Role(secondRoleObjectId.toString(), "secondName", authorityIds);

        authority1 = new Authority(firstAuthorityObjectId.toString(), "permission");
        authority2 = new Authority(secondAuthorityObjectId.toString(), "permission");

        user = User.builder()
                .id(UUID.randomUUID().toString())
                .username("user")
                .password("password")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roleIds(roleIds)
                .enabled(true)
                .build();
        userName = user.getUsername();
    }

    @Test
    void findByUsername_FindsByUserName_ReturnsUserDetails() {
        // Given
        SimpleGrantedAuthority simpleGrantedAuthority1 = new SimpleGrantedAuthority(authority1.getPermission());
        SimpleGrantedAuthority simpleGrantedAuthority2 = new SimpleGrantedAuthority(authority2.getPermission());
        Set<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>() {{
            add(simpleGrantedAuthority1);
            add(simpleGrantedAuthority2);
        }};
        User userWithAuthorities = user.withAuthorities(authorities);

        // When
        when(userRepository.findByName(userName)).thenReturn(Mono.just(user));
        when(roleRepository.findById(firstRoleObjectId)).thenReturn(Mono.just(role1));
        when(roleRepository.findById(secondRoleObjectId)).thenReturn(Mono.just(role2));
        when(authorityRepository.findById(firstAuthorityObjectId)).thenReturn(Mono.just(authority1));
        when(authorityRepository.findById(secondAuthorityObjectId)).thenReturn(Mono.just(authority2));
        Mono<UserDetails> userMono = userDetailsService.findByUsername(userName);

        // Then
        StepVerifier.create(userMono).expectNext(userWithAuthorities).verifyComplete();
    }

    @Test
    void findByUsername_UserExists_ReturnsUserDetailsWithoutAuthorities() {
        // Given
        User userWithoutAuthorities = user.withAuthorities(new HashSet<SimpleGrantedAuthority>());

        // When
        when(userRepository.findByName(userName)).thenReturn(Mono.just(user));
        when(roleRepository.findById(firstRoleObjectId)).thenReturn(Mono.just(role1));
        when(roleRepository.findById(secondRoleObjectId)).thenReturn(Mono.just(role2));
        when(authorityRepository.findById(any())).thenReturn(Mono.empty());
        Mono<UserDetails> userMono = userDetailsService.findByUsername(userName);

        // Then
        StepVerifier.create(userMono).expectNext(userWithoutAuthorities).verifyComplete();
    }
}