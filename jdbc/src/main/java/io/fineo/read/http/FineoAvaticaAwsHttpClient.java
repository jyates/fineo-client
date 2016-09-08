package io.fineo.read.http;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import io.fineo.client.AwsClient;
import io.fineo.read.AwsApiGatewayBytesTranslator;
import io.fineo.read.jdbc.ConnectionStringBuilder;
import io.fineo.read.jdbc.FineoConnectionProperties;
import org.apache.calcite.avatica.remote.AuthenticationType;
import org.apache.calcite.avatica.remote.AvaticaHttpClient;
import org.apache.calcite.avatica.remote.UsernamePasswordAuthenticateable;
import org.asynchttpclient.Response;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static io.fineo.read.jdbc.ConnectionPropertyUtil.setInt;
import static io.fineo.read.jdbc.FineoConnectionProperties.API_KEY;

/**
 * An Avatica client that writes/reads a Fineo AWS endpoint
 */
public class FineoAvaticaAwsHttpClient implements AvaticaHttpClient,
                                                  UsernamePasswordAuthenticateable {
  private final AwsApiGatewayBytesTranslator translator = new AwsApiGatewayBytesTranslator();
  private final Map<String, String> properties;
  private final AwsClient client;
  private StaticCredentialsProvider credentials;

  public FineoAvaticaAwsHttpClient(URL url) throws MalformedURLException, URISyntaxException {
    // simplify the url to just the bit we will actually send
    url = (
      url.getPort() == -1 ?
      new URL(url.getProtocol(), url.getHost(), url.getPath()) :
      new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath()));
    this.properties = ConnectionStringBuilder.parse(url);
    this.client = new AwsClient(url, "/prod");
    client.setApiKey(properties.get(API_KEY));
  }

  @Override
  public byte[] send(byte[] request) {
    client.setCredentials(this.credentials);
    request = translator.encode(request);
    try {
      Response response = client.post(request).get();
      return translator.decode(response.getResponseBodyAsBytes());
    } catch (InterruptedException | ExecutionException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private ClientConfiguration getClientConfiguration() {
    ClientConfiguration client = new ClientConfiguration();
    setInt(properties, FineoConnectionProperties.CLIENT_MAX_CONNECTIONS,
      prop -> client.withMaxConnections(prop));
    setInt(properties, FineoConnectionProperties.CLIENT_REQUEST_TIMEOUT,
      prop -> client.withSocketTimeout(prop));
    setInt(properties, FineoConnectionProperties.CLIENT_INIT_TIMEOUT,
      prop -> client.withConnectionTimeout(prop));
    setInt(properties, FineoConnectionProperties.CLIENT_MAX_ERROR_RETRY,
      prop -> client.withMaxErrorRetry(prop));
    return client;
  }

  @Override
  public void setUsernamePassword(AuthenticationType authType, String username, String password) {
    switch (authType) {
      case BASIC:
      case DIGEST:
        this.credentials =
          new StaticCredentialsProvider(new BasicAWSCredentials(username, password));
    }
  }

  public void close() {
    this.client.close();
  }
}
