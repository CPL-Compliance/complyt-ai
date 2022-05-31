package com.complyt.business.nexus;

import com.complyt.config.dao.CaliforniaEconomicNexusDao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaliforniaEconomicNexus {

    @Qualifier("californiaEconomicNexusDao")
    @NonNull
    private CaliforniaEconomicNexusDao californiaEconomicNexusDao;
}
