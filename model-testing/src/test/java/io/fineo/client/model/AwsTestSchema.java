package io.fineo.client.model;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import io.fineo.client.FineoApiClientException;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.schema.SchemaApi;
import io.fineo.client.model.schema.field.CreateFieldRequest;
import io.fineo.client.model.schema.field.ReadFieldResponse;
import io.fineo.client.model.schema.internal.CreateOrgRequest;
import io.fineo.client.model.schema.internal.InternalSchemaApi;
import io.fineo.client.model.schema.metric.DeleteMetricRequest;
import io.fineo.client.model.schema.metric.MetricRequest;
import io.fineo.client.model.schema.metric.ReadMetricResponse;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class AwsTestSchema {

  private static final String TEST_PROFILE_NAME = "tmp-user";
  private static final String TEST_ORG_ID_PREFIX = "itest-org-id-";
  private final String TEST_API_KEY = "pmV5QkC0RG7tHMYVdyvgG8qLgNV79Swh3XIiNsF1";
  private final String endpoint = "https://jemcyhqlji.execute-api.us-east-1.amazonaws.com";

  private final ProfileCredentialsProvider credentials = new ProfileCredentialsProvider
    (TEST_PROFILE_NAME);
  @Rule
  public AwsApiKeyRule keys = new AwsApiKeyRule(credentials, "jemcyhqlji", "prod");

  @Test
  public void test() throws Exception {
    FineoClientBuilder builder = new FineoClientBuilder()
      .withApiKey(TEST_API_KEY)
      .withCredentials(credentials)
      .withEndpoint(endpoint);

    // ensure that we have api keys for each org
    String org = keys.createApiKey(TEST_ORG_ID_PREFIX + System.currentTimeMillis());
    String orgAsync =
      keys.createApiKey(TEST_ORG_ID_PREFIX + "async-" + System.currentTimeMillis() + 1);

    InternalSchemaApi internal = builder.build(InternalSchemaApi.class);
    CreateOrgRequest request = new CreateOrgRequest();
    request.setOrgId(org);
    internal.create(request);

    // create a second org async
    request.setOrgId(orgAsync);
    internal.createAsync(request).get();

    SchemaApi sync = builder.withApiKey(org).build(SchemaApi.class);
    SchemaApi async = builder.withApiKey(orgAsync).build(SchemaApi.class);

    // create a metric for each org
    String metricName = "metric", metricAsync = "a_metric";
    MetricRequest createMetric = new MetricRequest();
    createMetric.setMetricName(metricName);

    sync.createMetric(createMetric);
    createMetric.setMetricName(metricAsync);
    async.createMetricAync(createMetric);

    ReadMetricResponse expectedResponse = new ReadMetricResponse();
    expectedResponse.setName(metricName);
    assertEquals(expectedResponse, sync.readMetric(metricName));
    ReadMetricResponse expectedResponseAsync = new ReadMetricResponse();
    expectedResponseAsync.setName(metricAsync);
    assertEquals(expectedResponseAsync, async.readMetricAync(metricAsync));

    // create fields for each metric
    createFields(sync::createField, metricName, getFields());
    createFields((field) -> {
      try {
        return async.createFieldAync(field).get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, metricAsync, getFields());

    setFields(expectedResponse, getFields());
    assertEquals(expectedResponse, sync.readMetric(metricName));
    setFields(expectedResponseAsync, getFields());
    assertEquals(expectedResponseAsync, async.readMetric(metricAsync));

    // delete the metrics
    sync.deleteMetric(new DeleteMetricRequest().setMetricName(metricName));
    async.deleteMetric(new DeleteMetricRequest().setMetricName(metricAsync));

    // and fail to read them the next time
    try {
      sync.readMetric(metricName);
      fail("should not have been able to read deleted metric!");
    } catch (FineoApiClientException e) {
      // expected
    }

    // and fail to read them the next time
    Future<ReadMetricResponse> read = async.readMetricAync(metricAsync);
    try {
      read.get();
      fail("should not have been able to read deleted metric!");
    } catch (FineoApiClientException e) {
      // expected
    }
  }

  private CreateFieldRequest[] getFields() {
    List<CreateFieldRequest> fields = new ArrayList<>();

    fields.add(new CreateFieldRequest().setFieldType("INT").setFieldName("f2"));
    fields.add(new CreateFieldRequest().setFieldType("BOOLEAN").setFieldName("f2"));
    fields.add(new CreateFieldRequest().setFieldType("DOUBLE").setFieldName("f3"));
    fields.add(new CreateFieldRequest().setFieldType("LONG").setFieldName("f4"));
    fields.add(new CreateFieldRequest().setFieldType("FLOAT").setFieldName("f5"));
    fields.add(new CreateFieldRequest().setFieldType("VARCHAR").setFieldName("f6"));

    return fields.toArray(new CreateFieldRequest[0]);
  }

  private void createFields(Function<CreateFieldRequest, Empty> func, String metricName,
    CreateFieldRequest... fields) {
    for (CreateFieldRequest field : fields) {
      field.setMetricName(metricName);
      func.apply(field);
    }
  }

  private void setFields(ReadMetricResponse response, CreateFieldRequest... fields) {
    List<ReadFieldResponse> read = new ArrayList<>();
    for (CreateFieldRequest field : fields) {
      read.add(new ReadFieldResponse().setName(field.getFieldName()).setType(field.getFieldType()));
    }
    response.setFields(read.toArray(new ReadFieldResponse[0]));
  }
}
