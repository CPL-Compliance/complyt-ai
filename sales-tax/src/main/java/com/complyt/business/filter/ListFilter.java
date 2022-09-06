package com.complyt.business.filter;

import java.util.List;

public interface ListFilter<T, Criteria> {
    List<T> filter(List<T> list, Criteria criteria);
}
