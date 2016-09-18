package io.fineo.client.model;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import io.fineo.client.FineoApiClientException;
import io.fineo.client.FineoClientBuilder;
import model.ApiKeyManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;

/**
 * Test against the live integration testing API deployed on AWS
 */
public class ITSimpleAwsApi {
  private static final Logger LOG = LoggerFactory.getLogger(ITSimpleAwsApi.class);

  private static final String TEST_USAGE_PLAN = "zrr0qh";
  private static final String INTEGRATION_API_ID = "bvnlz7s8gi";
  private static final String ENDPOINT =
    "https://bvnlz7s8gi.execute-api.us-east-1.amazonaws.com";
  private static final String PROFILE_FOR_TEST_INIT = "it-test-api";

  private static final AWSCredentialsProvider credentials = new ProfileCredentialsProvider
    (PROFILE_FOR_TEST_INIT);
  private static final ApiKeyManager keys = new ApiKeyManager(credentials, "prod",
    INTEGRATION_API_ID);

  private static String API_KEY;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @BeforeClass
  public static void setupApiKey() throws Exception {
    API_KEY = keys.createApiKey("it " + Instant.now(), TEST_USAGE_PLAN);
    waitForKey();
  }

  @AfterClass
  public static void deleteApiKey() {
    keys.deleteKey(API_KEY);
  }

  @Test
  public void testIamOnly() throws Exception {
    FineoClientBuilder builder = new FineoClientBuilder()
      .withCredentials(credentials)
      .withEndpoint(ENDPOINT);
    try (IntegrationTestingApi api = builder.build(IntegrationTestingApi.class)) {
      api.iamCredentialsOnlyRequired();
    }
  }

  @Test
  public void testSimpleObjectRead() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      SimpleObjectResponse response = api.simpleObject();
      assertTrue("Didn't get a message in the response!", response.getMessage().length() > 0);

      CompletableFuture<SimpleObjectResponse> async = api.simpleObjectAsync();
      response = async.get();
      assertTrue("Didn't get a message in the async response!", response.getMessage().length() > 0);
    }
  }

  @Test
  public void testPathWithField() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      api.interpolateFieldValue("file");
      // nulls are handled with randomly injecting a value
      api.interpolateFieldValue(null);
    }
  }

  @Test
  public void testPathWithFieldDefaultErrorHandling() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      thrown.expect(RuntimeException.class);
      api.interpolateFieldValueNullErrorDefaultHandling(null);
    }
  }

  @Test
  public void testErrorResponse() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      thrown.expect(FineoApiClientException.class);
      api.error();
    }
  }

  @Test
  public void testGetParameters() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      api.getWithParams("getField");
    }
  }

  @Test
  public void testBaseMethods() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      api.post();
      api.patch();
      api.put();
      api.delete();
      api.get();
    }
  }

  private static IntegrationTestingApi getApi() {
    FineoClientBuilder builder = new FineoClientBuilder()
      .withCredentials(credentials)
      .withApiKey(API_KEY)
      .withEndpoint(ENDPOINT);
    return builder.build(IntegrationTestingApi.class);
  }

  private static void waitForKey() throws Exception {
    try (IntegrationTestingApi api = getApi()) {
      long now = Instant.now().toEpochMilli();
      long wait = Duration.ofMinutes(3).toMillis();
      while (Instant.now().toEpochMilli() - now < wait) {
        try {
          LOG.info("Waiting for key to be added to usage plan...");
          api.get();
          LOG.info("Key works!");
          return;
        } catch (FineoApiClientException e) {
          if (e.getMessage().contains("\"message\":\"Forbidden\"")) {
            Thread.sleep(2000);
          } else {
            LOG.error("==== Error ==== \n{]", e.getMessage());
            throw e;
          }
        }
      }
      throw new Exception("Key was never authorized!");
    }
  }
}
