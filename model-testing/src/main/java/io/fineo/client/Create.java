package io.fineo.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.fineo.client.model.schema.ReadSchemaManagement;
import io.fineo.client.model.schema.SchemaApi.Field;
import io.fineo.client.model.schema.SchemaApi.Management;
import io.fineo.client.model.schema.SchemaApi.Metric;
import io.fineo.client.model.schema.field.CreateFieldRequest;
import io.fineo.client.model.schema.internal.CreateOrgRequest;
import io.fineo.client.model.schema.internal.InternalSchemaApi;
import io.fineo.client.model.schema.metric.CreateMetricRequest;
import io.fineo.e2e.external.ApiUtils;
import io.fineo.e2e.external.schema.options.ApiOption;
import io.fineo.e2e.external.schema.options.FieldOption;
import io.fineo.e2e.external.schema.options.MetricOption;
import io.fineo.e2e.external.schema.options.OrgOption;
import model.ApiKeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/**
 *
 */
public class Create implements Command<Map<String, Object>> {
  private static final Logger LOG = LoggerFactory.getLogger(Create.class);

  private final ApiOption api;
  private final OrgOption org;
  private final MetricOption metrics;
  private final FieldOption fields;

  private String key;

  public Create(OrgOption org, MetricOption metrics, FieldOption fields, ApiOption api) {
    this.api = api;
    this.org = org;
    this.metrics = metrics;
    this.fields = fields;
  }

  @Override
  public Map<String, Object> doGet() throws Exception {
    ClientConfiguration conf = new ClientConfiguration();
    try (ApiAwsClient internalClient = new ApiAwsClient(ApiUtils.getUrl(api.internal), api.stage, conf);
         ApiAwsClient externalClient = new ApiAwsClient(ApiUtils.getUrl(api.external), api.stage, conf)) {
      internalClient.setApiKey(api.key);
      internalClient.setCredentials(api.credentials.get());

      if (api.userKey == null) {
        LOG.info("Creating user api key...");
        key = createKey();
        LOG.info("Initializing schema for user.");
        createOrg(internalClient, key);
      } else {
        key = api.userKey;
      }
      LOG.info("Using user API key: [{}]", key);

      // now from the perspective of that customer (api key)
      externalClient.setApiKey(key);
      externalClient.setCredentials(api.credentials.get());
      FineoClientBuilder.ApiClientHandler handler =
        new FineoClientBuilder.ApiClientHandler(externalClient);
      Management schema = FineoClientBuilder.build(Management.class, handler);
      Metric metrics = FineoClientBuilder.build(Metric.class, handler);
      Field fields = FineoClientBuilder.build(Field.class, handler);

      // aws is eventually consistent, so we have to wait to ensure the key is added to the plan
      waitForKeyToBeAdded(schema);

      LOG.info("Creating metrics");
      makeMetrics(metrics);
      LOG.info("Creating fields");
      makeFields(fields);

      LOG.info("Schema Created!");
      Map<String, Object> out = new HashMap<>();
      out.put("id", key);
      return out;
    }
  }

  private String createKey() throws MalformedURLException {
    ApiKeyManager keys = new ApiKeyManager(api.credentials.get(), api.stage, api.external);
    return keys.createApiKey(org.org, api.plan.toArray(new String[0]));
  }

  private void createOrg(ApiAwsClient client, String orgId) {
    FineoClientBuilder.ApiClientHandler handler = new FineoClientBuilder.ApiClientHandler(client);
    InternalSchemaApi internal = FineoClientBuilder.build(InternalSchemaApi.class, handler);
    CreateOrgRequest request = new CreateOrgRequest();
    request.setOrgId(orgId);
    internal.create(request);
  }

  private void makeMetrics(Metric schema) throws Exception {
    List<String> names = metrics.metricNames;
    Multimap<String, String> aliases = ArrayListMultimap.create();
    for (String alias : metrics.metricAliases) {
      String[] parts = alias.split(".");
      aliases.put(parts[0], parts[1]);
    }
    for (String name : names) {
      makeMetric(schema, name, newArrayList(aliases.get(name)));
    }
  }

  private void waitForKeyToBeAdded(Management client) throws InterruptedException {
    while (true) {
      try {
        LOG.info("Waiting for key to be added to usage plan...");
        client.getCurrentSchemaManagement(new ReadSchemaManagement());
        LOG.info("Key works!");
        return;
      } catch (FineoApiClientException e) {
        if (e.getMessage().contains("Forbidden")) {
          Thread.sleep(2000);
        } else {
          throw e;
        }
      }
    }
  }

  private void makeMetric(Metric client, String metric, List<String> aliases)
    throws Exception {
    CreateMetricRequest request = new CreateMetricRequest();
    request.setMetricName(metric);
    request.setAliases(aliases.toArray(new String[0]));
    client.createMetric(request);
  }

  private void makeFields(Field schema) {
    // parse the fields
    Multimap<String, String> metricToFields = ArrayListMultimap.create();
    Multimap<String, String> metricFieldToAliases = ArrayListMultimap.create();
    Map<String, String> metricFieldToType = new HashMap<>();
    for (String name : fields.fieldName) {
      String[] parts = name.split("[.]");
      String metric = parts[0];
      String field = parts[1];
      metricToFields.put(metric, field);
    }

    for (String type : fields.fieldType) {
      String[] parts = type.split("[.]");
      String metricField = format("%s.%s", parts[0], parts[1]);
      metricFieldToType.put(metricField, parts[2]);
    }

    for (String alias : fields.fieldAliases) {
      String[] parts = alias.split("[.]");
      String metricField = format("%s.%s", parts[0], parts[1]);
      metricFieldToAliases.put(metricField, parts[2]);
    }

    for (Map.Entry<String, String> entry : metricToFields.entries()) {
      CreateFieldRequest createField = new CreateFieldRequest();
      createField.setMetricName(entry.getKey());
      createField.setFieldName(entry.getValue());
      String key = format("%s.%s", entry.getKey(), entry.getValue());
      createField.setFieldType(metricFieldToType.get(key));
      createField.setAliases(newArrayList(metricFieldToAliases.get(key)).toArray(new String[0]));
      schema.createField(createField);
    }
  }

  @Override
  public void cleanup() {
    if (key != null) {
      LOG.info("Deleting API key: {}", key);
      ApiKeyManager keys = new ApiKeyManager(api.credentials.get(), api.stage, api.external);
      keys.deleteKey(key);
    }
  }
}
