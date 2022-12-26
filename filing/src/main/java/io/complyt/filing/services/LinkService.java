package io.complyt.filing.services;

import io.complyt.filing.domain.Link;
import io.complyt.filing.repositories.LinkRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LinkService {
    @NonNull
    LinkRepository linkRepository;

    public Mono<Link> getOne() {
        return linkRepository.getOne();
    }
}
