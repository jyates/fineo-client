package io.fineo.e2e.external.write;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fineo.client.model.write.SingleStreamEventBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class WriteEventsOption {

  @Parameter(names = "--event-type", description = "Logical event name to look up in EventTypes",
             variableArity = true)
  public List<String> types = new ArrayList<>();

  @Parameter(names = "--event", variableArity = true,
             description = "[type].<field>.<value> Type only necessary if more than one type "
                           + "specified")
  public List<String> fields = new ArrayList<>();

  public SingleStreamEventBase[] getEvents() throws IOException {
    if (types.size() == 0) {
      return null;
    }
    List<SingleStreamEventBase> events = new ArrayList<>();
    for (String type : types) {
      events.add(getEvent(type));
    }
    return events.toArray(new SingleStreamEventBase[0]);
  }

  private <T extends SingleStreamEventBase> T getEvent(String type) throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> event = new HashMap<>();
    boolean timestamp = false;
    for (String field : fields) {
      String[] parts = field.split("[.]");
      int i = 1;
      if (parts.length == 2) {
        i = 0;
        event.put("metrictype", type);
      } else {
        if (!parts[0].equals(type)) {
          continue;
        }
      }
      event.put(parts[i], parts[i + 1]);
      if (parts[i].equals("timestamp")) {
        timestamp = true;
      }
    }

    if (timestamp == false) {
      event.put("timestamp", System.currentTimeMillis());
    }

    String msg = mapper.writeValueAsString(event);
    return (T) mapper.readValue(msg, EventTypes.EVENTS.get(type));
  }

  private String quote(String s) {
    return format("\"%s\"", s);
  }
}
