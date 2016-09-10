package io.fineo.client;

import com.amazonaws.auth.AWSCredentialsProvider;

/**
 *
 */
public class ExposedFineoClientBuilder {

  private FineoClientBuilder builder = new FineoClientBuilder();

  public FineoClientBuilder withApiKey(String apiKey) {
    return builder.withApiKey(apiKey);
  }

  public FineoClientBuilder withEndpoint(String endpoint) {
    return builder.withEndpoint(endpoint);
  }

  public FineoClientBuilder withStage(String stage) {
    return builder.withStage(stage);
  }

  public <T> T build(Class<T> apiClass) {
    return builder.build(apiClass);
  }

  public FineoClientBuilder witConfiguration(ClientConfiguration conf) {
    return builder.witConfiguration(conf);
  }

  public static <T> T build(Class<T> apiClass, FineoClientBuilder.ApiClientHandler handler) {
    return FineoClientBuilder.build(apiClass, handler);
  }

  public FineoClientBuilder withCredentials(AWSCredentialsProvider credentials) {
    return builder.withCredentials(credentials);
  }
}
