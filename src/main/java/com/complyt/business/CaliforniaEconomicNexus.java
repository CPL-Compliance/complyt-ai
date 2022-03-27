package com.complyt.business;

import com.complyt.dao.CaliforniaEconomicNexusDao;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaliforniaEconomicNexus {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private int threshold;

    @Qualifier("CaliforniaEconomicNexusDao")
    @NonNull
    private CaliforniaEconomicNexusDao californiaEconomicNexusDao;

    public CaliforniaEconomicNexus(CaliforniaEconomicNexusDao californiaEconomicNexusDao){
        this.californiaEconomicNexusDao = californiaEconomicNexusDao;
        threshold = californiaEconomicNexusDao.getThreshold();
    }

    public boolean isValueExceeds(int value){

        return value > threshold;
    }
}
