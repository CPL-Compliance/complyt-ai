package com.complyt.domain.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Builder
@ToString
@With
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private Set<ObjectId> authorityIds;
    private Set<Authority> authorities;
}