package com.complyt.security;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TenantResolverTest {
//
//    @Autowired
//    TenantResolver tenantResolver;
//
//    @MockBean
//    SecurityContext securityContext;
//
//    @Test
//    void resolve() {
//        // Given
//        Jwt jwt = Jwt.withTokenValue(UUID.randomUUID().toString())
//                .header("Alg", "aaa")
//                .claim("tenant_id", "tenant")
//                .build();
//
//        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
//
//        // When
//        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
//        SecurityContextHolder.setContext(securityContext);
//
//        Mono<String> tenantMono = tenantResolver.resolve();
//
//        // Then
//        StepVerifier.create(tenantMono).expectNext("tenant").verifyComplete();
//    }
}