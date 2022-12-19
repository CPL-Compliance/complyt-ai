package io.complyt.testService;

import lombok.Generated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Generated
@RestController
public class Controller {

    @PreAuthorize("hasAuthority('SCOPE_read:transaction')")
    @GetMapping("/hi")
    public Mono<String> hi() {
        return Mono.just("hi :)");
    }

    @PreAuthorize("hasAuthority('SCOPE_read:rates')")
    @GetMapping("/bye")
    public Mono<String> bye() {
        return Mono.just("bye :(");
    }
}
