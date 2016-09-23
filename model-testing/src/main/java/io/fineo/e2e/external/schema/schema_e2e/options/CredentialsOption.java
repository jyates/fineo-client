package io.fineo.e2e.external.schema.schema_e2e.options;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class CredentialsOption {

  private static final Logger LOG = LoggerFactory.getLogger(CredentialsOption.class);

  private AWSCredentialsProvider credentials;

  @Parameter(names = "--credential-type", description = "profile, static")
  public String credentialType;

  @Parameter(names = "--profile-name",
             description = "Name of the profile to use when loading creds")
  public String profileName;

  @Parameter(names = "--static-key", description = "Key for the credentials")
  public String staticKey;

  @Parameter(names = "--static-secret", description = "Key for the credentials")
  public String staticSecret;

  public AWSCredentialsProvider get() {
    if (this.credentials == null) {
      credentials = getCredentials();
    }
    return this.credentials;
  }

  private AWSCredentialsProvider getCredentials() {
    switch (credentialType) {
      case "profile":
        LOG.info("Using profile credentials  - [{}]", profileName);
        return new ProfileCredentialsProvider(profileName);
      case "static":
        LOG.info("Using static credentials  - [{}]", staticKey);
        return new StaticCredentialsProvider(new BasicAWSCredentials(staticKey, staticSecret));
      default:
        throw new IllegalArgumentException("No valid credentials provided!");
    }
  }
}
