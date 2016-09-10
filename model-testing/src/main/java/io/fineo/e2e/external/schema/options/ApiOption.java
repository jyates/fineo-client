package io.fineo.e2e.external.schema.options;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ApiOption {

  @Parameter(names = "--api-internal", description = "Api endpoint to call for internal operations")
  public String internal;

  @Parameter(names = "--api-external", description = "Api endpoint to call for external operations")
  public String external;

  @Parameter(names = "--stage", description = "Api endpoint stage")
  public String stage = "prod";

  @Parameter(names = "--api-key", description = "Api key to use")
  public String key;

  @Parameter(names = "--user-api-key")
  public String userKey;

  @Parameter(names = "--plan", description = "Usage plan Id to which to add new api keys",
             variableArity = true)
  public List<String> plan = new ArrayList<>();

  @ParametersDelegate
  public CredentialsOption credentials = new CredentialsOption();
}
