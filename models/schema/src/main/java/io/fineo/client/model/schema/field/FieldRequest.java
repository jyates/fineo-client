package io.fineo.client.model.schema.field;

import io.fineo.client.model.schema.metric.MetricRequest;

public class FieldRequest extends MetricRequest {

  private String fieldName;

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
}
