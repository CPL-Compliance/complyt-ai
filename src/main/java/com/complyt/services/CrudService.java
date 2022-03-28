package com.complyt.services;

import com.complyt.domain.Order;

import java.util.List;

public interface CrudService<T, ID> {
    T save(T object);
    T findByName(String name);
    T findById(ID id);
}
