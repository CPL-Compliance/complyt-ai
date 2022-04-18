package com.complyt.config.dao;

import com.complyt.repositories.StateRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaliforniaEconomicNexusDao {

    @Qualifier("stateRepository")
    @NonNull
    StateRepository stateRepository;
}