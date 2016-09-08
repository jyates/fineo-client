package io.fineo.client.model.schema.internal;


import io.fineo.client.Op;
import io.fineo.client.model.Empty;

import java.util.concurrent.CompletableFuture;

public interface InternalSchemaApi {

  // General Schema management

  @Op(path = "/schema_internal/org", method = "PUT")
  Empty create(CreateOrgRequest create);

  @Op(path = "/schema_internal/org", method = "PUT")
  CompletableFuture<Empty> createAsync(CreateOrgRequest create);
}
