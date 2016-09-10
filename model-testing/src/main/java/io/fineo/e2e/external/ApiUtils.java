package io.fineo.e2e.external;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

/**
 *
 */
public class ApiUtils {
  public static URL getUrl(String apiId) throws MalformedURLException {
    return new URL(format("https://%s.execute-api.us-east-1.amazonaws.com", apiId));
  }
}
