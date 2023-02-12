package com.complyt.domain.properties;

import com.complyt.domain.State;

public interface StateProperty {
    State getState();
    StateProperty withState(State state);
}
