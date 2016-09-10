package io.fineo.client.model;

import com.amazonaws.auth.AWSCredentialsProvider;
import model.ApiKeyManager;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class AwsApiKeyRule extends ExternalResource {

  private static final Logger LOG = LoggerFactory.getLogger(AwsApiKeyRule.class);
  private final ApiKeyManager manager;
  private List<String> ids = new ArrayList<>();

  public AwsApiKeyRule(AWSCredentialsProvider credentials, String apiId, String stage) {
    this.manager = new ApiKeyManager(credentials, stage, apiId);
  }

  public String createKey(String description, String planId){
    String key = this.manager.createApiKey(description, planId);
    ids.add(key);
    return key;
  }

  @Override
  protected void after() {
    for (String id : ids) {
      try {
        manager.deleteKey(id);
      } catch (Exception e) {
        LOG.error("Failed to delete api key: " + id, e);
      }
    }
    super.after();
  }
}
