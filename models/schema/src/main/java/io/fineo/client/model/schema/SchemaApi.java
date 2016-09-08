package io.fineo.client.model.schema;


import io.fineo.client.Op;
import io.fineo.client.Parameter;
import io.fineo.client.model.Empty;
import io.fineo.client.model.schema.field.CreateFieldRequest;
import io.fineo.client.model.schema.field.ReadFieldResponse;
import io.fineo.client.model.schema.field.UpdateFieldRequest;
import io.fineo.client.model.schema.metric.DeleteMetricRequest;
import io.fineo.client.model.schema.metric.MetricRequest;
import io.fineo.client.model.schema.metric.ReadMetricResponse;
import io.fineo.client.model.schema.metric.UpdateMetricRequest;

import java.util.concurrent.CompletableFuture;

public interface SchemaApi {

  // General Schema management

  @Op(path = "/schema", method = "GET")
  ReadSchemaManagement getCurrentSchemaManagement(ReadSchemaManagement read);

  @Op(path = "/schema", method = "GET")
  CompletableFuture<ReadSchemaManagement> getCurrentSchemaManagementAsync(
    ReadSchemaManagement read);

  @Op(path = "/schema", method = "PATCH")
  Empty updateCurrentSchemaManagement(SchemaManagementRequest update);

  @Op(path = "/schema", method = "PATCH")
  CompletableFuture<Empty> updateCurrentSchemaManagementAsync(SchemaManagementRequest update);

  // Metrics

  @Op(path = "/schema/metric", method = "POST")
  Empty createMetric(MetricRequest update);

  @Op(path = "/schema/metric", method = "POST")
  CompletableFuture<Empty> createMetricAync(MetricRequest update);

  @Op(path = "/schema/metric", method = "GET")
  ReadMetricResponse readMetric(@Parameter(name = "metricName") String metricName);

  @Op(path = "/schema/metric", method = "GET")
  CompletableFuture<ReadMetricResponse> readMetricAync(
    @Parameter(name = "metricName") String metricName);

  @Op(path = "/schema/metric", method = "PATCH")
  Empty updateMetric(UpdateMetricRequest update);

  @Op(path = "/schema/metric", method = "PATCH")
  CompletableFuture<Empty> updateMetricAsync(UpdateMetricRequest update);

  @Op(path = "/schema/metric", method = "DELETE")
  Empty deleteMetric(DeleteMetricRequest update);

  @Op(path = "/schema/metric", method = "DELETE")
  CompletableFuture<Empty> deleteMetricAsync(DeleteMetricRequest update);

  // Fields

  @Op(path = "/schema/field", method = "POST")
  Empty createField(CreateFieldRequest request);

  @Op(path = "/schema/field", method = "POST")
  CompletableFuture<Empty> createFieldAync(CreateFieldRequest request);

  @Op(path = "/schema/field", method = "GET")
  ReadFieldResponse readField(@Parameter(name = "fieldName") String fieldName);

  @Op(path = "/schema/field", method = "GET")
  CompletableFuture<ReadFieldResponse> createFieldAync(
    @Parameter(name = "metricName") String metricName,
    @Parameter(name = "fieldName") String fieldName);

  @Op(path = "/schema/field", method = "PATCH")
  Empty updateField(UpdateFieldRequest update);

  @Op(path = "/schema/field", method = "PATCH")
  CompletableFuture<Empty> updateFieldAsync(UpdateFieldRequest update);
}
