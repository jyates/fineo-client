package io.fineo.e2e.external;

@FunctionalInterface
public interface ThrowingSupplier<T> {
  T doGet() throws Exception;
}
