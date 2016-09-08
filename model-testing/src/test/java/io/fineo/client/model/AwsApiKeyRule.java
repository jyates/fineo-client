package io.fineo.client.model;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.CreateApiKeyRequest;
import com.amazonaws.services.apigateway.model.CreateApiKeyResult;
import com.amazonaws.services.apigateway.model.DeleteApiKeyRequest;
import com.amazonaws.services.apigateway.model.StageKey;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


public class AwsApiKeyRule extends ExternalResource {

  private static final Logger LOG = LoggerFactory.getLogger(AwsApiKeyRule.class);
  private List<String> ids = new ArrayList<>();
  private final String api;
  private final String stage;
  private final AmazonApiGatewayClient client;

  public AwsApiKeyRule(ProfileCredentialsProvider credentials, String apiId, String stage) {
    this.client = new AmazonApiGatewayClient(credentials);
    this.api = apiId;
    this.stage = stage;
  }

  /**
   * Create an API key
   * @param name
   * @return
   */
  public String createApiKey(String name) {
    StageKey stage = new StageKey();
    stage.setRestApiId(api);
    stage.setStageName(this.stage);
    CreateApiKeyRequest createApiKey = new CreateApiKeyRequest();
    createApiKey.setDescription("Testing key");
    createApiKey.setName(name);
    createApiKey.setStageKeys(newArrayList(stage));
    createApiKey.setEnabled(true);
    CreateApiKeyResult result = client.createApiKey(createApiKey);
    this.ids.add(result.getId());
    return result.getId();
  }

  @Override
  protected void after() {
    for (String id : ids) {
      try {
        DeleteApiKeyRequest request = new DeleteApiKeyRequest();
        request.withApiKey(id);
        client.deleteApiKey(request);
      } catch (Exception e) {
        LOG.error("Failed to delete api key: " + id);
      }
    }
    super.after();
  }
}
