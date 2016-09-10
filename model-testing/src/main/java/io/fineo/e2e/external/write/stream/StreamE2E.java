package io.fineo.e2e.external.write.stream;

import com.beust.jcommander.JCommander;
import io.fineo.client.ExposedFineoClientBuilder;
import io.fineo.client.FineoClientBuilder;
import io.fineo.client.model.write.SingleStreamEventBase;
import io.fineo.client.model.write.StreamRecordsResponse;
import io.fineo.client.model.write.StreamWrite;
import io.fineo.e2e.external.write.WriteApiOption;
import io.fineo.e2e.external.write.WriteEventsOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class StreamE2E {

  private static final Logger LOG = LoggerFactory.getLogger(
    io.fineo.e2e.external.write.batch.BatchE2E.class);

  public static void main(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      args = getArgs();
    }

    WriteApiOption api = new WriteApiOption();
    WriteEventsOption write = new WriteEventsOption();
    JCommander jc = new JCommander(new Object[]{api, write});
    jc.parse(args);

    FineoClientBuilder builder = new ExposedFineoClientBuilder()
      .withStage(api.stage)
      .withApiKey(api.key)
      .withEndpoint(api.getApi())
      .withCredentials(api.credentials.get());

    try (StreamWrite stream = builder.build(StreamWrite.class)) {
      SingleStreamEventBase[] event = write.getEvents();
      if (event != null) {
        StreamRecordsResponse response = stream.write(event);
        String eventString = Arrays.toString(event);
        assert response.getFailedRecordCount() == 0 :
          "Got some failed records when writing to stream! " +
          "\n events: \n" + eventString
          + "\n results: \n" + Arrays.toString(response.getRecords());
        LOG.info("Wrote events {}", Arrays.toString(event));
      }

    }
  }

  private static String[] getArgs() {
    return new String[]{
      "--api", "jo6nz6kjbj",
      "--api-key", "jAzNSmeY3x7U971bF1Qql56FtSyyWzk71b0Qxx1D",
      "--credential-type", "profile",
      "--profile-name", "tmp-user",
      "--event-type", "metric",
      "--event", "field.1",
//      "--remote-file", "s3://api-batch-processing-test.io.fineo.io/remote-s3-file_metric.json"
    };
  }
}
