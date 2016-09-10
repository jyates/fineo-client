package io.fineo.e2e.external.write.batch;

import io.fineo.client.model.write.SingleStreamEventBase;

import java.util.HashMap;
import java.util.Map;

/**
 * static mapping of event types to a concrete class
 */
public class EventTypes {

  public static Map<String, Class<? extends SingleStreamEventBase>> EVENTS = new HashMap<>();
  static {
    EVENTS.put("metric", Metric.class);
  }
  public static class Metric extends SingleStreamEventBase{
    private String field;

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }
  }
}
