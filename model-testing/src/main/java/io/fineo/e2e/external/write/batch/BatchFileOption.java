package io.fineo.e2e.external.write.batch;


import com.beust.jcommander.Parameter;

import java.net.URI;
import java.net.URISyntaxException;

public class BatchFileOption{

  @Parameter(names = "--remote-file", description = "Remote file to ingest")
  public String fileName;

  public String getFileName() throws URISyntaxException {
    if (fileName == null) {
      return null;
    }
    if (!fileName.startsWith("s3://")) {
      fileName = new URI("s3://" + fileName).toString();
    }
    return fileName;
  }
}
