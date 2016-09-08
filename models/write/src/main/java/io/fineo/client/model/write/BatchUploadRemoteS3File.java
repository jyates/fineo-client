package io.fineo.client.model.write;

/**
 *
 */
public class BatchUploadRemoteS3File {
  //  "Remote S3 file to batch read. Format: s3://<bucket>/<file key>"
  private String FilePath;

  public String getFilePath() {
    return FilePath;
  }

  public void setFilePath(String filePath) {
    FilePath = filePath;
  }
}
