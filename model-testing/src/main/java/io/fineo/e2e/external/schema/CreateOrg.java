package io.fineo.e2e.external.schema;

import com.beust.jcommander.JCommander;
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

  public static void main(String[] args) throws Exception {
    ApiOption api = new ApiOption();
    OrgOption org = new OrgOption();
    JCommander jc = new JCommander(new Object[]{api, org});
    jc.parse(args);

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
