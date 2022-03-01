package com.complyt.controller;

import com.complyt.services.ISalesTaxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/complyt")
public class ComplytController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ISalesTaxService salesTaxService;

    @GetMapping("/getSalesTax")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
        return salesTaxService.getSalesTax(zip, address, city, state);
    }
}