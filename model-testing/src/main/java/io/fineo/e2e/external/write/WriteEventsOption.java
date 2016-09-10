package io.fineo.e2e.external.write;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import io.fineo.client.model.write.SingleStreamEventBase;
import io.fineo.e2e.external.write.EventTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    List<String> elems = new ArrayList<>();
    boolean timestamp = false;
    for (String field : fields) {
      String[] parts = field.split("[.]");
      int i = 1;
      if (parts.length == 2) {
        i = 0;
      } else {
        if (!parts[0].equals(type)) {
          continue;
        }
      }
      String fieldName = format("\"%s\"", parts[i]);
      elems.add(Joiner.on(" : ").join(fieldName, parts[i + 1]));
      if (parts[i].equals("timestamp")) {
        timestamp = true;
      }
    }

    if (timestamp == false) {
      elems.add(format("\"timestamp\" : %s", System.currentTimeMillis()));
    }

    StringBuffer sb = new StringBuffer("{");
    Joiner.on(", \n").appendTo(sb, elems);
    return (T) mapper.readValue(sb.append("}").toString(), EventTypes.EVENTS.get(type));
  }
}
