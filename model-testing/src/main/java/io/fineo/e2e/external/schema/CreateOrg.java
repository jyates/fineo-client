package io.fineo.e2e.external.schema;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.schema.internal.CreateOrgRequest;
import io.fineo.client.model.schema.internal.InternalSchemaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create an Org with the internal schema
 */
public class CreateOrg {

  private static final Logger LOG = LoggerFactory.getLogger(CreateOrg.class);

  @ParametersDelegate
  private ApiOption api = new ApiOption();

  @ParametersDelegate
  private OrgOption org = new OrgOption();

  @Parameter(names = {"-h", "--help"}, help = true)
  private boolean help = false;

  public static void main(String[] args) throws Exception {
    CreateOrg create = new CreateOrg();
    JCommander jc = new JCommander(new Object[]{create});
    jc.parse(args);
    if (create.help) {
      jc.usage();
      System.exit(0);
    }

    create.run();
  }

  private void run() throws Exception {
    LOG.debug("Using api-key [{}]", api.key);
    try (InternalSchemaApi schema = new FineoClientBuilder()
      .withEndpoint(api.url)
      .withCredentials(api.credentials.get())
      .withApiKey(api.key)
      .build(InternalSchemaApi.class)) {

      schema.create(new CreateOrgRequest().setOrgId(org.id));
    }
  }
}
