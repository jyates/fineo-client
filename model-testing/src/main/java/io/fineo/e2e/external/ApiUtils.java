package io.fineo.e2e.external;

import com.google.common.base.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

/**
 *
 */
public class ApiUtils {
  public static URL getUrl(String apiId) {
    Preconditions.checkNotNull(apiId, "API ID must be non-null!");
    try {
      return new URL(format("https://%s.execute-api.us-east-1.amazonaws.com", apiId));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
