package io.complyt.filing.services;

import io.complyt.filing.domain.Link;
import io.complyt.filing.repositories.LinkRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FilingService {
    @NonNull
    LinkRepository linkRepository;
    public Mono<Link> getOne() {
        return linkRepository.getOne();
    }
}
