package com.complyt.proxies;


import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "TEST-APPLICATION")
public interface TestServiceProxy {
    @GetMapping("/hi")
    Mono<String> hi();

    @GetMapping("/bye")
    Mono<String> bye();
}
