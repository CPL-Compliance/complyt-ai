package io.complyt.authentication.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenRepositoryTest {
    @InjectMocks
    TokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
    }
}