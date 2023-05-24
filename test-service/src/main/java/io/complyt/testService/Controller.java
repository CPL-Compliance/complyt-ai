package io.complyt.testService;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Generated
@RestController
@Slf4j
public class Controller {

    @PreAuthorize("hasAuthority('SCOPE_read:transaction')")
    @GetMapping("/hi")
    public Mono<String> hi() {
        return Mono.just("hi :)");
    }

    @PreAuthorize("hasAuthority('SCOPE_read:sales_tax_rates')")
    @GetMapping("/bye")
    public Mono<String> bye() {
        return Mono.just("bye :(");
    }
}
