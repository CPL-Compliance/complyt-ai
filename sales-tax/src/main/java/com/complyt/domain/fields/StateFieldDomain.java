package com.complyt.domain.fields;

import com.complyt.domain.State;

public interface StateFieldDomain {
    State getState();
    StateFieldDomain withState(State state);
}
