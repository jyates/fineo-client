package io.fineo.client.model;

import io.fineo.client.Op;
import io.fineo.client.Parameter;

import java.util.concurrent.CompletableFuture;

public interface IntegrationTestingApi extends AutoCloseable {

  @Op(path = "/", method = "PUT")
  Empty put();

  @Op(path = "/", method = "DELETE")
  Empty delete();

  @Op(path = "/", method = "POST")
  Empty post();

  @Op(path = "/", method = "PATCH")
  Empty patch();

  @Op(path = "/", method = "GET")
  Empty get();

  @Op(path = "/", method = "GET")
  ParamResponse getWithParams(@Parameter(name = "metricName") String name);

  @Op(path = "/simple-object-response", method = "PUT")
  SimpleObjectResponse simpleObject();

  @Op(path = "/simple-object-response", method = "PUT")
  CompletableFuture<SimpleObjectResponse> simpleObjectAsync();

  @Op(path = "/iam-only", method = "PUT")
  Empty iamCredentialsOnlyRequired();

  @Op(path = "/path/{file}", method = "PUT")
  Empty interpolateFieldValue(
    @Parameter(name = "file", type = Parameter.Type.PATH, nullStrategy = Parameter.Strategy.RANDOM)
      String file
  );

  @Op(path = "/path/{file}", method = "PUT")
  Empty interpolateFieldValueNullErrorDefaultHandling(
    @Parameter(name = "file", type = Parameter.Type.PATH) String file
  );

  @Op(path = "/error-response", method = "GET")
  Empty error();
}
