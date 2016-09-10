package io.fineo.client;


import io.fineo.e2e.external.ThrowingSupplier;

/**
 *
 */
public interface Command<T> extends ThrowingSupplier<T> {

  public void cleanup();
}
