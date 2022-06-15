package com.complyt.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientTest {
    private Client client;

    @Test
    void CreateClient_NewClient_ClientCreated(){
        ObjectId id = new ObjectId();
        String name = "Client";
        client = new Client(id, name);

        Assertions.assertEquals(id, client.getId());
        Assertions.assertEquals(name, client.getName());
    }
}