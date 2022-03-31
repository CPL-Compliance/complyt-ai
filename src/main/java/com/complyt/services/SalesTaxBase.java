package com.complyt.services;

import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public abstract class SalesTaxBase {
    protected RestTemplate restTemplate;
}