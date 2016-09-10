package io.fineo.e2e.external.write.batch;


import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import io.fineo.client.model.write.SingleStreamEventBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class WriteOptions {

  @Parameter(names = "--event-type", description = "Logical event name to look up in EventTypes")
  public String type;

  @Parameter(names = "--event", variableArity = true,
             description = "<field>.<value>")
  public List<String> fields = new ArrayList<>();

  @Parameter(names = "--remote-file", description = "Remote file to ingest")
  public String fileName;

  public String getFileName() throws URISyntaxException {
    if (fileName == null) {
      return null;
    }
    if (!fileName.startsWith("s3://")) {
      fileName = new URI("s3://" + fileName).toString();
    }
    return fileName;
  }

  public <T extends SingleStreamEventBase> T getEvent() throws IOException {
    if (type == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();

    List<String> elems = new ArrayList<>();
    boolean timestamp = false;
    for (String field : fields) {
      String[] parts = field.split("[.]");
      parts[0] = format("\"%s\"", parts[0]);
      elems.add(Joiner.on(" : ").join(parts));
      if (parts[0].equals("timestamp")) {
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
