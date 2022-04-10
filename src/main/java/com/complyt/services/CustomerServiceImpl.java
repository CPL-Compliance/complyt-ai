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

    private CustomerRepository customerRepository;

    @Override
    public Customer save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer upsert(@NonNull Customer customer){
        return customerRepository.upsert(customer);
    }

    public Customer findOneByName(@NonNull String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public List<Customer> findByName(@NonNull String name) {
        return customerRepository.findByName(name);
    }

    @Override
    public Customer findById(@NonNull String id) {
        return null;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.getAll();
    }

}