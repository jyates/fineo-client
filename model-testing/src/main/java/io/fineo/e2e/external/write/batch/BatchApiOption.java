package io.fineo.e2e.external.write.batch;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.e2e.external.schema.options.CredentialsOption;

/**
 *
 */
public class BatchApiOption {

  @Parameter(names = "--api", description = "Api endpoint to call for external operations")
  public String external;

  @Parameter(names = "--stage", description = "Api endpoint stage")
  public String stage = "prod";

  @Parameter(names = "--api-key", description = "Api key to use")
  public String key;

  @ParametersDelegate
  public CredentialsOption credentials = new CredentialsOption();
}
