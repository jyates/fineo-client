package io.fineo.e2e.external.write.batch;

import com.beust.jcommander.JCommander;
import io.fineo.client.ExposedFineoClientBuilder;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.write.BatchUploadRemoteS3File;
import io.fineo.client.model.write.BatchWrite;
import io.fineo.client.model.write.SingleStreamEventBase;
import io.fineo.e2e.external.write.WriteApiOption;
import io.fineo.e2e.external.write.WriteEventsOption;
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

    WriteApiOption api = new WriteApiOption();
    BatchFileOption write = new BatchFileOption();
    WriteEventsOption events = new WriteEventsOption();
    JCommander jc = new JCommander(new Object[]{api, write, events});
    jc.parse(args);

    FineoClientBuilder builder = new ExposedFineoClientBuilder()
      .withStage(api.stage)
      .withApiKey(api.key)
      .withEndpoint(api.getApi())
      .withCredentials(api.credentials.get());

    try(BatchWrite batch = builder.build(BatchWrite.class)){
      String file = write.getFileName();
      if(file != null){
        batch.uploadS3File(new BatchUploadRemoteS3File().setFilePath(file));
      }
      SingleStreamEventBase[] eventsToWrite = events.getEvents();
      String fileName = UUID.randomUUID().toString();
      if(eventsToWrite != null){
        batch.write(fileName,eventsToWrite);
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
//      "--remote-file", "s3://api-batch-processing-test.io.fineo.io/remote-s3-file_metric.json"
    };
  }
}
