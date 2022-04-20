package com.complyt.business;

import com.complyt.config.dao.CaliforniaEconomicNexusDao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaliforniaEconomicNexus {

    //private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Qualifier("californiaEconomicNexusDao")
    @NonNull
    private CaliforniaEconomicNexusDao californiaEconomicNexusDao;
}
