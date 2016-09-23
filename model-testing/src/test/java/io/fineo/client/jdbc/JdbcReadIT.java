package io.fineo.client.jdbc;

import io.fineo.e2e.external.ApiUtils;
import io.fineo.read.Driver;
import io.fineo.read.jdbc.FineoConnectionProperties;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * JDBC manual testing of a Read API.
 * <p>
 * All properties are loaded through environment variables, including authentication.
 * The variables to set are:
 * <ol>
 *   <li>PROFILE_NAME</li>
 *   <li>FINEO_API_KEY</li>
 *   <li>FINEO_API_ID</li>
 * </ol>
 */
public class JdbcReadIT {

  private static final String API_KEY = System.getenv("FINEO_API_KEY");
  private static final String INTEGRATION_API_ID = System.getenv("FINEO_API_ID");
  private static final String ENDPOINT = ApiUtils.getUrl(INTEGRATION_API_ID).toString();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @BeforeClass
  public static void setup() {
    Driver.load();
  }

  @Test
  public void testRequireApiKey() throws Exception {
    Properties props = getProperties();
    props.remove(FineoConnectionProperties.AUTHENTICATION.camelName());
    thrown.expect(RuntimeException.class);
    DriverManager.getConnection(getUrl(), props);
  }

  @Test
  public void testMetadata() throws Exception {
    try (Connection conn = DriverManager.getConnection(getUrl(), getProperties())) {
      DatabaseMetaData meta = conn.getMetaData();
      try (ResultSet rs = meta.getCatalogs()) {
        assertTrue("No catalogs found!", rs.next());
        assertEquals("Wrong catalog name!", "FINEO", rs.getString("TABLE_CAT"));
        assertFalse("More than one catalog found!", rs.next());
      }

      try (ResultSet rs = meta.getSchemas()) {
        assertTrue("No schemas found!", rs.next());
        assertEquals("No schemas found!", "FINEO", rs.getString("TABLE_SCHEM"));
        assertFalse("More than one schema found!", rs.next());
      }
    }
  }

  private Properties getProperties() {
    Properties props = new Properties();
    props.setProperty(FineoConnectionProperties.API_KEY.camelName(), API_KEY);
    props.setProperty(FineoConnectionProperties.AUTHENTICATION.camelName(), "profile");
    props.setProperty(FineoConnectionProperties.PROFILE_CREDENTIAL_NAME.camelName(), System
      .getenv("PROFILE_NAME"));
    return props;
  }

  private String getUrl() {
    return format("jdbc:fineo:url=%s", ENDPOINT);
  }
}
