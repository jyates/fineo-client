package io.fineo.e2e.external.schema;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.e2e.external.schema.schema_e2e.options.CredentialsOption;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ApiOption {
  @Parameter(names = "--url", description = "URL to call for api operations")
  public String url;

  @Parameter(names = "--stage", description = "Api endpoint stage")
  public String stage = "prod";

  @Parameter(names = "--api-key", description = "Api key to use")
  public String key;

  @ParametersDelegate
  public CredentialsOption credentials = new CredentialsOption();
}
