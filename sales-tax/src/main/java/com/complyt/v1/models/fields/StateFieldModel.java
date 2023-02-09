package com.complyt.v1.models.fields;

import com.complyt.domain.State;

public interface StateFieldModel {
    State state();
    StateFieldModel withState(State state);
}
