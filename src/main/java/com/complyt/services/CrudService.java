package com.complyt.services;

import java.util.List;

public interface CrudService<T, ID> {
    T save(T object);
    T findOneByName(String name);
    List<T> findByName(String name);
    T findById(ID id);
    List<T> findAll();
}
