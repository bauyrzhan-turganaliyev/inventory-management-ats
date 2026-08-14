package it.unifi.bautur.store.repository;

import java.util.function.Function;

@FunctionalInterface
public interface TransactionCode<T>
        extends Function<RepositoryProvider, T> {
}