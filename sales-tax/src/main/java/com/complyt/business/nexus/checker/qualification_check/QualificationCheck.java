package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;

public interface QualificationCheck<T> {
    boolean isQualified(T t, @NonNull NexusStateRule nexusStateRule);
}
