package com.complyt.controller;

import com.complyt.model.Client;
import com.complyt.model.Customer;
import com.complyt.model.Order;
import com.complyt.model.State;
import com.complyt.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class ComplytController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SalesTaxService salesTaxByAddressService;

    @Autowired
    CustomerService customerService;

    @Autowired
    ClientService clientService;

    @Autowired
    OrderService orderService;

    @Autowired
    StateService stateService;

    @GetMapping("/getSalesTax")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
        return salesTaxByAddressService.getSalesTax(zip, address, city, state);
    }

    @PostMapping("/createCustomer")
    public Customer createCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer);
    }

    @PostMapping("/createClient")
    public Client createClient(@RequestBody Client client){
        if(client.getOrders() != null && client.getOrders().size() > 0){
            orderService.save(client.getOrders());
        }

        return clientService.save(client);
    }

    @GetMapping("/getCustomerByName")
    public List<Customer> getCustomerByName(@RequestParam String name){
        return customerService.getCustomerByName(name);
    }

    @GetMapping("/getClientByName")
    public Client getClientByName(@RequestParam String name){
        return clientService.getClient(name);
    }

    @PostMapping("/addOrderToClient")
    public void addOrderToClient(@RequestParam String client, @RequestBody Order order){
        customerService.save(order.getCustomer());
        orderService.save(order);
        clientService.addOrderToClient(client, order);
    }

    @GetMapping("/getOrder")
    public Order addOrderToClient(@RequestParam String orderId){
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/getAllCustomers")
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/getState")
    public State getState(@RequestParam String name){
        return stateService.getState(name);
    }
}