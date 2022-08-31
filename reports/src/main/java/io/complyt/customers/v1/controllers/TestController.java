package io.complyt.customers.v1.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("")
    public String hello(){
        return "hello from dummy container";
    }
}
