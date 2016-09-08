package io.fineo.client.model.schema.field;


public class CreateFieldRequest extends FieldRequest {

  private String fieldType;

  public String getFieldType() {
    return fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = fieldType;
  }
}
