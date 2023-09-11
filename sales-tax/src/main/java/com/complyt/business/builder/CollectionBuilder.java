package com.complyt.business.builder;

import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;

import java.util.Collection;

public interface CollectionBuilder<T> {
    Collection<T> build(@NonNull Transaction transaction);
}
