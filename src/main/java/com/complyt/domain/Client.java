package com.complyt.domain;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@ToString
@Document(collection = "client")
public class Client {
}
