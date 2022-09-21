package com.complyt.utils.filter;

import java.util.List;

public interface ListFilter<T, Criteria> {
    List<T> filter(List<T> list, Criteria criteria);
}
