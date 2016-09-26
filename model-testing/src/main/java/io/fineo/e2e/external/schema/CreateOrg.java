package io.fineo.e2e.external.schema;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.client.FineoApiClientException;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.schema.ReadSchemaManagement;
import io.fineo.client.model.schema.internal.CreateOrgRequest;
import io.fineo.client.model.schema.internal.InternalSchemaApi;
import io.fineo.client.tools.option.ApiOption;
import io.fineo.client.tools.option.HelpOption;
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

  @ParametersDelegate
  private HelpOption help = HelpOption.help();

  public static void main(String[] args) throws Exception {
    CreateOrg create = new CreateOrg();
    JCommander jc = new JCommander(new Object[]{create});
    jc.parse(args);
    create.help.check(jc);
    create.run();
  }

  private void run() throws Exception {
    LOG.debug("Using api-key [{}]", api.key);
    try (InternalSchemaApi schema = new FineoClientBuilder()
      .withEndpoint(api.url)
      .withCredentials(api.credentials.get())
      .withApiKey(api.key)
      .build(InternalSchemaApi.class)) {

      while (true) {
        try {
          schema.create(new CreateOrgRequest().setOrgId(org.id));
          LOG.info("---- Completed schema creation ------ ");
          return;
        } catch (FineoApiClientException e) {
          if (e.getMessage().contains("Forbidden")) {
            LOG.info("Waiting for key to be added to usage plan...");
            Thread.sleep(2000);
          } else {
            throw e;
          }
        }

      }
    }
  }
}
