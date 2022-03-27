package com.complyt.dao;

import com.complyt.repositories.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaliforniaEconomicNexusDao {
    @Autowired
    StateRepository stateRepository;

    public int getThreshold() {
        return 0;
    }
}