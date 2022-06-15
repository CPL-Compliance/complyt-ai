package com.complyt.domain.security;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@ToString
@AllArgsConstructor
@With
@Document(collection = "authority")
public class Authority {
    @Id
    private String id;
    private String permission;
}