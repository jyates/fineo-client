package io.fineo.client.model;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.schema.internal.CreateOrgRequest;
import io.fineo.client.model.schema.internal.InternalSchemaApi;
import org.junit.Test;

/**
 *
 */
public class AwsTestSchema {

  private static final String TEST_PROFILE_NAME = "tmp-user";
  private static final String TEST_ORG_ID_PREFIX = "itest-org-id-";
  private final String TEST_API_KEY = "pmV5QkC0RG7tHMYVdyvgG8qLgNV79Swh3XIiNsF1";
  private final String endpoint = "https://jemcyhqlji.execute-api.us-east-1.amazonaws.com";

  @Test
  public void test() throws Exception {
    FineoClientBuilder builder = new FineoClientBuilder()
      .withApiKey(TEST_API_KEY)
      .withCredentials(new ProfileCredentialsProvider(TEST_PROFILE_NAME))
      .withEndpoint(endpoint);

    String org = TEST_ORG_ID_PREFIX + System.currentTimeMillis();
    String orgAsync = TEST_ORG_ID_PREFIX + System.currentTimeMillis() + 1;

    InternalSchemaApi internal = builder.build(InternalSchemaApi.class);
    CreateOrgRequest request = new CreateOrgRequest();
    request.setOrgId(org);
    internal.create(request);

    // create a second org async
    request.setOrgId(orgAsync);
    internal.createAsync(request).get();
  }
}
