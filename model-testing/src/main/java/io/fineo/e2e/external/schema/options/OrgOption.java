package io.fineo.e2e.external.schema.options;

import com.beust.jcommander.Parameter;

/**
 *
 */
public class OrgOption {

  @Parameter(names = "--org", description="ID of the org")
  public String org;
}
