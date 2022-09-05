package com.complyt.business.filter;

import java.util.List;

public interface ListFilter<T, Rule> {
    List<T> filter(List<T> list, Rule rule);
}
