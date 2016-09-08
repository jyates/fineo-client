package io.fineo.read.http;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.http.HttpMethodName;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class AwsClient implements AutoCloseable{

  private final String prefix;
  private final URI uri;
  private AWSCredentialsProvider credentials;
  private String apiKey;
  private final DefaultAsyncHttpClient client;
  private final String url;

  public AwsClient(URL url, String envPrefix) throws URISyntaxException {
    this.url = url.toExternalForm();
    this.client = new DefaultAsyncHttpClient();
    this.prefix = envPrefix;
    String uri = url.toURI().toString();
    this.uri = new URI(uri.substring(0, uri.length() - prefix.length()));
  }

  // get doesn't have a body
  public Future<Response> get(Map<String, List<String>> parameters) throws URISyntaxException {
    BoundRequestBuilder get = client.prepareGet(this.url);
    return prepare(get, HttpMethodName.GET, new byte[0], parameters).execute();
  }

  public Future<Response> post(byte[] data) throws URISyntaxException {
    return prepare(client.preparePost(this.url), data, HttpMethodName.POST)
      .execute();
  }

  public Future<Response> patch(byte[] data) throws URISyntaxException {
    return prepare(client.preparePatch(this.url), data, HttpMethodName.PATCH)
      .execute();
  }

  public Future<Response> delete(byte[] data) throws URISyntaxException {
    return prepare(client.preparePatch(this.url), data, HttpMethodName.DELETE)
      .execute();
  }

  private BoundRequestBuilder prepare(BoundRequestBuilder request, byte[] data, HttpMethodName
    method)
    throws
    URISyntaxException {
    return prepare(request, method, data, Collections.emptyMap());
  }

  private BoundRequestBuilder prepare(BoundRequestBuilder request, HttpMethodName
    method, byte[] data, Map<String, List<String>> query) throws URISyntaxException {
    request.setBody(data);
    if (credentials != null) {
      DefaultRequest<AmazonWebServiceRequest> awsReq = new DefaultRequest("execute-api");
      awsReq.setContent(new ByteArrayInputStream(data));
      awsReq.addHeader("Content-Length", Integer.toString(data.length));
      awsReq.addHeader("Content-Type", "application/json");

      awsReq.setHttpMethod(method);
      awsReq.setEndpoint(uri);
      awsReq.setResourcePath(prefix);
      awsReq.setParameters(query);
      awsReq.addHeader("x-api-key", apiKey);

      AWS4Signer signer = new AWS4Signer();
      signer.setServiceName("execute-api");
      signer.setRegionName("us-east-1");
      signer.sign(awsReq, credentials.getCredentials());

      for (Map.Entry<String, String> header : awsReq.getHeaders().entrySet()) {
        request.addHeader(header.getKey(), header.getValue());
      }

      request.setQueryParams(awsReq.getParameters());
      return request;
    }
    return request;
  }

  public void setCredentials(AWSCredentialsProvider credentials) {
    this.credentials = credentials;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public void close(){
    this.client.close();
  }
}
