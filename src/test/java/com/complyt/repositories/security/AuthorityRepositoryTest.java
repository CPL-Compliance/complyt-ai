package com.complyt.repositories.security;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorityRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;
    @Test
    void findById() {
        ObjectId objectId = new ObjectId("628e213f7cc3a0367607b253");
        authorityRepository.findById(objectId).subscribe(System.out::println);
    }
}