package com.complyt.v1.validators.body_checkers;

import com.complyt.business.address.StateMap;

public interface StateExistsChecker {
    static String check(String state){
        return state != null ? StateMap.statesToStandartizedState.get(state.toUpperCase().trim()) : null;
    }
}
