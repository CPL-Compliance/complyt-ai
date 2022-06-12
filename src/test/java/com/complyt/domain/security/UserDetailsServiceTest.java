package com.complyt.domain.security;


import com.complyt.repositories.security.AuthorityRepository;
import com.complyt.repositories.security.RoleRepository;
import com.complyt.repositories.security.UserRepository;
import com.complyt.security.UserDetailsService;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    void findByUsername_FindsByUserName_ReturnsUserDetails() {
        // Given
        ObjectId firstRoleObjectId = new ObjectId();
        ObjectId secondRoleObjectId = new ObjectId();
        Set<ObjectId> roleIds = new HashSet<ObjectId>() {{
            add(firstRoleObjectId);
            add(secondRoleObjectId);
        }};

        ObjectId firstAuthorityObjectId = new ObjectId();
        ObjectId secondAuthorityObjectId = new ObjectId();
        Set<ObjectId> authorityIds = new HashSet<ObjectId>() {{
            add(firstAuthorityObjectId);
            add(secondAuthorityObjectId);
        }};

        Role role1 = new Role(firstRoleObjectId.toString(),"firstName",authorityIds);
        Role role2 = new Role(secondRoleObjectId.toString(),"secondName",authorityIds);

        Authority authority1 = new Authority(firstAuthorityObjectId.toString(),"permission");
        Authority authority2 = new Authority(secondAuthorityObjectId.toString(),"permission");

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .username("user")
                .password("password")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roleIds(roleIds)
                .enabled(true)
                .build();
        String userName = user.getUsername();

        SimpleGrantedAuthority simpleGrantedAuthority1 = new SimpleGrantedAuthority(authority1.getPermission());
        SimpleGrantedAuthority simpleGrantedAuthority2 = new SimpleGrantedAuthority(authority2.getPermission());
        Set<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>(){{
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
}