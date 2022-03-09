package com.complyt.repository;

import com.complyt.entity.State;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureDataMongo
public class StateRepositoryTest {

    @Autowired
    StateRepository stateRepository;

    @Test
    public void testFindByName(){
        String california = "California";
        List<State> states = stateRepository.findByName(california);
        
    }
}