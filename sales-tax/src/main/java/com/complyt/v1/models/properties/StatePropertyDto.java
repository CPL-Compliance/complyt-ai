package com.complyt.v1.models.properties;

import com.complyt.domain.State;
import com.complyt.v1.models.StateDto;

import java.util.UUID;

public interface StatePropertyDto {
    StateDto state();
    StatePropertyDto withState(StateDto state);
}
