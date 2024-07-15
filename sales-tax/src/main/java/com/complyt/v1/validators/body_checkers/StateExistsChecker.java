package com.complyt.v1.validators.body_checkers;

import com.complyt.business.address.StateMap;

public interface StateExistsChecker {
    public static String check(String state){
        return state != null ? StateMap.statesToStandartizedState.get(state.toUpperCase().trim()) : null;

    }
}
