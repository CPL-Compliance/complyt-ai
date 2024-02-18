package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.v1.models.ApiKeyDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Component
public class QueryParamsExtractorCredentials implements QueryParamsExtractor<ApiKeyDto> {
    @Override
    public Mono<ApiKeyDto> extract(@NotNull ServerRequest serverRequest) {
        Optional<MediaType> mediaType = serverRequest.headers().contentType();

        if (mediaType.isPresent() && mediaType.get().equals(MediaType.APPLICATION_FORM_URLENCODED)) {
            return serverRequest.formData()
                    .flatMap(formData -> {
                        Map<String, String> singleValueMap = formData.toSingleValueMap();

                        return Mono.just(new ApiKeyDto(singleValueMap.get("clientId"), singleValueMap.get("clientSecret")));
                    });
        }

        return Mono.empty();
    }
}