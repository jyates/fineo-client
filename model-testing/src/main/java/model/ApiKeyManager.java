package model;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.CreateApiKeyRequest;
import com.amazonaws.services.apigateway.model.CreateApiKeyResult;
import com.amazonaws.services.apigateway.model.CreateUsagePlanKeyRequest;
import com.amazonaws.services.apigateway.model.DeleteApiKeyRequest;
import com.amazonaws.services.apigateway.model.StageKey;

import java.util.ArrayList;
import java.util.List;

public class ApiKeyManager {

  private final String[] api;
  private final String stage;
  private final AmazonApiGatewayClient client;

  public ApiKeyManager(AWSCredentialsProvider credentials, String stage, String... apiId) {
    this.client = new AmazonApiGatewayClient(credentials);
    this.api = apiId;
    this.stage = stage;
  }

  public String createApiKey(String name, String... usagePlans) {
    List<StageKey> stages = new ArrayList<>();
    for (String apiId : api) {
      StageKey stage = new StageKey();
      stage.setRestApiId(apiId);
      stage.setStageName(this.stage);
      stages.add(stage);
    }
    CreateApiKeyRequest createApiKey = new CreateApiKeyRequest();
    createApiKey.setDescription("Testing key");
    createApiKey.setName(name);
    createApiKey.setStageKeys(stages);
    createApiKey.setEnabled(true);
    CreateApiKeyResult result = client.createApiKey(createApiKey);

    for (String usagePlan : usagePlans) {
      CreateUsagePlanKeyRequest planKey = new CreateUsagePlanKeyRequest();
      planKey.setKeyId(result.getId());
      planKey.setUsagePlanId(usagePlan);
      planKey.setKeyType("API_KEY");
      client.createUsagePlanKey(planKey);
    }
    return result.getId();
  }

  public void deleteKey(String keyId) {
    DeleteApiKeyRequest request = new DeleteApiKeyRequest();
    request.withApiKey(keyId);
    client.deleteApiKey(request);
  }
}
