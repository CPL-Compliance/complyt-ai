package com.complyt.v1.models.properties;

import com.complyt.domain.State;

public interface StateFieldPropertyDto {
    State state();
    StateFieldPropertyDto withState(State state);
}
