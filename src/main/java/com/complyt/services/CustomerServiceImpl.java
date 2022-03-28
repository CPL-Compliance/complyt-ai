package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    //private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CustomerRepository customerRepository;

    @Override
    public Customer create(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer findOneByName(String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public List<Customer> findByName(String name) {
        return null;
    }

    @Override
    public Customer findById(String id) {
        return null;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.getAllCustomers();
    }

    @Override
    public Customer save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }
}