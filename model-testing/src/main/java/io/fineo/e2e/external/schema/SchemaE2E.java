package io.fineo.e2e.external.schema;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import io.fineo.client.Command;
import io.fineo.client.Create;
import io.fineo.client.FineoApiClientException;
import io.fineo.e2e.external.schema.options.ApiOption;
import io.fineo.e2e.external.schema.options.FieldOption;
import io.fineo.e2e.external.schema.options.MetricOption;
import io.fineo.e2e.external.schema.options.OrgOption;
import io.fineo.e2e.external.schema.options.OutputOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Create an org, metric and set of fields. Alternative command to delete the same
 */
public class SchemaE2E {

  private static final Logger LOG = LoggerFactory.getLogger(SchemaE2E.class);

  public static void main(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      args = getArgs();
    }
    LOG.info("Got args: {}", Joiner.on("\n").join(args));
    OutputOption out = new OutputOption();
    OrgOption org = new OrgOption();
    ApiOption api = new ApiOption();
    MetricOption metrics = new MetricOption();
    FieldOption fields = new FieldOption();
    Create create = new Create(org, metrics, fields, api);

    JCommander jc = new JCommander(new Object[]{org, api, metrics, fields, out});
    jc.addCommand("create", create);
    jc.parse(args);

    LOG.debug("Using api-key [{}]", api.key);
    String cmd = jc.getParsedCommand();
    Command<Map<String, Object>> command =
      (Command<Map<String, Object>>) jc.getCommands().get(cmd)
                                       .getObjects().get(0);
    Map<String, Object> map;
    try {
      map = command.doGet();
    } catch (Exception e) {
      try {
        command.cleanup();
      } catch (Exception e2) {
        LOG.error("Failed to cleanup command {}", command, e2);
      }
      if (e instanceof FineoApiClientException) {
        LOG.error("Failed making method: {}, code: {}, request-id: {} ", (
            (FineoApiClientException) e).getMethod(), ((FineoApiClientException) e).getStatusCode()
          , ((FineoApiClientException) e).getRequestId());
      }
      throw e;
    }
    ObjectMapper mapper = new ObjectMapper();
    String mapString = mapper.writeValueAsString(map);
    LOG.info("Got event: \n{}", mapString);
    out.write(mapString);
  }

  private static String[] getArgs() {
    return new String[]{
      " --api-internal", "jemcyhqlji",
      "--api-external", "mlmxu4b05d",
      "--api-key", "pmV5QkC0RG7tHMYVdyvgG8qLgNV79Swh3XIiNsF1",
      // Plans: schema, writes
      "--plan", "5sq3mg", "8hzbrr",
      "--credential-type", "profile",
      "--profile-name", "tmp-user",
      "--org", "test-invoke-api-key",
      "--metric", "metric",
      "--field", "metric.field",
      "--type", "metric.field.INTEGER",
//      "--user-api-key", "pUMPYgmUhZ5M8fMrtbLDD2RTyjBGQ7qz5YTS1R97",
      "create"
    };
  }
}
