package io.fineo.client;

/**
 *
 */
public class FineoApiClientException extends RuntimeException{
  private int statusCode;
  private String requestId;

  public FineoApiClientException(String message) {
    super(message);
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getRequestId() {
    return requestId;
  }
}
