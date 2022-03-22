package com.complyt.v1.model;

import com.complyt.domain.Address;
import com.complyt.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ClientDto {
    private String name;
    private Address address;
    private List<Order> orders;
}