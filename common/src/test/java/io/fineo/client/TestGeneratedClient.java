package io.fineo.client;

import org.junit.Test;
import org.mockito.Mockito;


public class TestGeneratedClient {

  @Test
  public void testAutoCloseable() throws Exception {
    ApiAwsClient client = Mockito.mock(ApiAwsClient.class);
    try (FakeApi api = FineoClientBuilder.build(FakeApi.class, new FineoClientBuilder
      .ApiClientHandler
      (client))) {
    }

    Mockito.verify(client).close();
  }
}
