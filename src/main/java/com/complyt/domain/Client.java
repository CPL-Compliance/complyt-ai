package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Document(collection = "client")
public class Client {
    @Id
    private ObjectId id;
    private String name;
}