package io.complyt.v1.validators.address_body_checks;

import io.complyt.business.collection_fetcher.StateMap;

public interface StateExistsChecker {
    static String check(String state){
        return state != null ? StateMap.statesToStandartizedState.get(state.toUpperCase().trim()) : null;
    }
}
