package com.complyt.domain.security;

import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@ToString
@Document(collection = "role")
public class Role {
    @Id
    private String id;
    private String name;
    private Set<ObjectId> authorityIds;
}
