package com.complyt.domain.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@ToString
@Document(collection = "authority")
public class Authority {
    @Id
    private String id;
    private String role;
}