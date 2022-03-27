package com.complyt.services;

import com.complyt.v1.model.StateDto;

public interface StateService {
    StateDto findByName(String name);
}