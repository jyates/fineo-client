import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import io.fineo.client.tools.option.CredentialsOption;
import io.fineo.schema.aws.dynamodb.DynamoDBRepository;
import io.fineo.schema.store.SchemaStore;
import io.fineo.schema.store.StoreClerk;
import org.schemarepo.ValidatorFactory;

import java.io.FileNotFoundException;

/**
 *
 */
public class RawStoreReader {

  @Parameter(names = "--dynamo-url")
  private String dynamo;

  @Parameter(names = "--dynamo-table")
  private String table;

  @Parameter(names = "--org")
  private String org;

  @ParametersDelegate
  private CredentialsOption credentials = new CredentialsOption();

  public static void main(String[] args) throws Exception {
    RawStoreReader reader = new RawStoreReader();
    JCommander commander = new JCommander(reader);
    commander.parse(args);

    reader.run();
  }

  private void run() throws FileNotFoundException {
    AmazonDynamoDBAsyncClient client = new AmazonDynamoDBAsyncClient(credentials.get());
    if (dynamo.contains(":")) {
      client.setEndpoint(dynamo);
    } else {
      client.setRegion(Region.getRegion(Regions.fromName(dynamo)));
    }
    DynamoDBRepository repo = new DynamoDBRepository(ValidatorFactory.EMPTY, client, table);
    SchemaStore store = new SchemaStore(repo);
    StoreClerk clerk = new StoreClerk(store, org);
    System.out.println("Org: "+org);
    for (StoreClerk.Metric metric : clerk.getMetrics()) {
      System.out.println("Metric: " + metric.getUserName());
      String prefix = "  ";
      print(prefix, "id: " + metric.getMetricId());
      print(prefix, "aliases: " + metric.getAliases());
      print(prefix, "timestamps: " + metric.getTimestampPatterns());
      printField(metric.getTimestampField());
      for (StoreClerk.Field f : metric.getUserVisibleFields()) {
        printField(f);
      }
    }
  }

  private void printField(StoreClerk.Field f) {
    String prefix = "    ";
    print(prefix, "Field: " + f.getName());
    prefix = prefix + " ";
    print(prefix, "id:" + f.getCname());
    print(prefix, "type: " + f.getType());
    print(prefix, "aliases:" + f.getAliases());
    print(prefix, "internal:" + f.isInternalField());
  }

  private void print(String prefix, String content) {
    System.out.println(prefix + content);
  }
}
