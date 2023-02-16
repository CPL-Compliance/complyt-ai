package com.complyt.v1.models.properties;

import com.complyt.v1.models.StateDto;

public interface StateFieldPropertyDto {
    StateDto state();

    StateFieldPropertyDto withState(StateDto state);
}
