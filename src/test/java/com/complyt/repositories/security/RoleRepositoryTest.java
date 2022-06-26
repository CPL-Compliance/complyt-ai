package com.complyt.repositories.security;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.security.Role;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(SecurityConfigMockTest.class)
public class RoleRepositoryTest {

    @InjectMocks
    RoleRepository roleRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Role role;

    @BeforeEach
    void setUp() {
        ObjectId roleId = new ObjectId();
        Set<ObjectId> authorityIds = new HashSet<ObjectId>() {{
            add(new ObjectId());
        }};

        role = new Role(roleId.toString(),"name",authorityIds);
    }

    @Test
    void findById_FindsRoleById_ReturnsRole() {
        // Given
        ObjectId roleId = new ObjectId(role.getId());

        // When
        when(reactiveMongoTemplate.findById(roleId,Role.class)).thenReturn(Mono.just(role));
        Mono<Role> roleMono = roleRepository.findById(roleId);

        // Then
        StepVerifier.create(roleMono).expectNext(role).verifyComplete();
    }


}
