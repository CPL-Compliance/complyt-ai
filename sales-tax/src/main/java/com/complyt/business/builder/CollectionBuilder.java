package com.complyt.business.builder;

import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import lombok.NonNull;

import java.util.Collection;

public interface CollectionBuilder<T> {
    Collection<T> build(@NonNull Transaction transaction);
}
