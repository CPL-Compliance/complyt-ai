package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface NexusStateRuleService extends CrudService<NexusStateRule, String> {
    Mono<NexusStateRule> findByState(@NonNull String state);
}
