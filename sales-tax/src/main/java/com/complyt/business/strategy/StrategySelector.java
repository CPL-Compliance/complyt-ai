package com.complyt.business.strategy;

import java.util.function.Function;

public interface StrategySelector<T> {
    Function select(T t);
}
