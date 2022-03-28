package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {

        return customerRepository.save(customer);
    }

    public List<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }

    public List<Customer> findAll() {

        return customerRepository.getAllCustomers();
    }

    public Customer save(Customer customer) {
        if (customer == null) {
            return null;
        }

        return customerRepository.save(customer);
    }
}