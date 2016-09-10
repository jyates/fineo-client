package io.fineo.e2e.external.write;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.e2e.external.ApiUtils;
import io.fineo.e2e.external.schema.options.CredentialsOption;

import java.net.MalformedURLException;

/**
 *
 */
public class WriteApiOption {

  @Parameter(names = "--api", description = "Api endpoint to call for api operations")
  public String api;

  @Parameter(names = "--stage", description = "Api endpoint stage")
  public String stage = "prod";

  @Parameter(names = "--api-key", description = "Api key to use")
  public String key;

  @ParametersDelegate
  public CredentialsOption credentials = new CredentialsOption();

  public String getApi() throws MalformedURLException {
    return ApiUtils.getUrl(api).toString();
  }
}
