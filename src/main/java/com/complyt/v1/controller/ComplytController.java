package com.complyt.v1.controller;

import com.complyt.domain.Client;
import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.service.*;
import com.complyt.v1.model.StateDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ComplytController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SalesTaxService salesTaxService;

    @Autowired
    CustomerService customerService;

    @Autowired
    ClientService clientService;

    @Autowired
    OrderService orderService;

    @Autowired
    StateService stateService;

    @GetMapping("/salesTax")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
        return salesTaxService.getSalesTax(zip, address, city, state);
    }

    @PostMapping("/customer")
    public Customer createCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer);
    }

    @PostMapping("/client")
    public Client createClient(@RequestBody @NotNull Client client){
        if(client.getOrders() != null && client.getOrders().size() > 0){
            orderService.save(client.getOrders());
        }

        return clientService.save(client);
    }

    @GetMapping("/customer")
    public List<Customer> getCustomerByName(@RequestParam String name){
        return customerService.getCustomerByName(name);
    }

    @GetMapping("/client")
    public Client getClientByName(@RequestParam String name){
        return clientService.getClient(name);
    }

    @PostMapping("/order/addToClient")
    public void addOrderToClient(@RequestParam String client, @RequestBody @NotNull Order order){
        customerService.save(order.getCustomer());
        orderService.save(order);
        clientService.addOrderToClient(client, order);
    }

    @GetMapping("/Order")
    public Order addOrderToClient(@RequestParam String orderId){
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/customer/all")
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/state")
    public StateDto getState(@RequestParam String name){
        return stateService.getState(name);
    }
}