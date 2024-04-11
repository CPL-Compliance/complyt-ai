package com.complyt.domain.nexus;

import lombok.Value;

@Value
public record Subsidiary(String subsidiaryId, String name) {
}