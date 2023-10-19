package io.complyt.authentication.v1.validators.query_params;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class QueryParamsExtractorEmptyTest {
    QueryParamsExtractorEmpty<Object> queryParamsExtractorEmpty;

    @Mock
    ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        queryParamsExtractorEmpty = new QueryParamsExtractorEmpty<>();
    }

    @Test
    void extract_serverRequest_returnMonoEmpty() {
        StepVerifier.create(queryParamsExtractorEmpty.extract(serverRequest)).verifyComplete();
    }
}