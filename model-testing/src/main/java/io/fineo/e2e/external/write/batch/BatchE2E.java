package io.fineo.e2e.external.write.batch;

import com.beust.jcommander.JCommander;
import io.fineo.client.ExposedFineoClientBuilder;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.write.BatchUploadRemoteS3File;
import io.fineo.client.model.write.BatchWrite;
import io.fineo.client.model.write.SingleStreamEventBase;
import io.fineo.e2e.external.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 *
 */
public class BatchE2E {

  private static final Logger LOG = LoggerFactory.getLogger(BatchE2E.class);

  public static void main(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      args = getArgs();
    }

    BatchApiOption api = new BatchApiOption();
    WriteOptions write = new WriteOptions();
    JCommander jc = new JCommander(new Object[]{api, write});
    jc.parse(args);

    FineoClientBuilder builder = new ExposedFineoClientBuilder()
      .withStage(api.stage)
      .withApiKey(api.key)
      .withEndpoint(ApiUtils.getUrl(api.external).toString())
      .withCredentials(api.credentials.get());

    try(BatchWrite batch = builder.build(BatchWrite.class)){
      String file = write.getFileName();
      if(file != null){
        batch.uploadS3File(new BatchUploadRemoteS3File().setFilePath(file));
      }
      SingleStreamEventBase event = write.getEvent();
      String fileName = UUID.randomUUID().toString();
      if(event != null){
        batch.write(fileName, new SingleStreamEventBase[]{event});
        LOG.info("Wrote batch of rows as file: {}", fileName);
      }

    }
  }

  private static String[] getArgs() {
    return new String[]{
      "--api", "fcj1n5lot9",
      "--api-key", "jAzNSmeY3x7U971bF1Qql56FtSyyWzk71b0Qxx1D",
      "--credential-type", "profile",
      "--profile-name", "tmp-user",
      "--event-type", "metric",
      "--event", "field.1",
//      "--remote-file", "s3://external-batch-processing-test.io.fineo.io/remote-s3-file_metric.json"
    };
  }
}
