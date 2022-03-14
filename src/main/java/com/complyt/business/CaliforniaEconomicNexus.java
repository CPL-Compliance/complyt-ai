package com.complyt.business;

import com.complyt.dao.CaliforniaEconomicNexusDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaliforniaEconomicNexus {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private int threshold = 0;

    //@Autowired
    //private CaliforniaEconomicNexusDao californiaEconomicNexusDao;

    public CaliforniaEconomicNexus(/*CaliforniaEconomicNexusDao californiaEconomicNexusDao*/){
        //this.californiaEconomicNexusDao = californiaEconomicNexusDao;
        //threshold = californiaEconomicNexusDao.getThreshold();
    }

    public boolean isValueExceeds(int value){
        boolean isExceeds = value > threshold;

        return isExceeds;
    }
}
