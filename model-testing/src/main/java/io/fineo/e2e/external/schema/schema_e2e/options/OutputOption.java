package io.fineo.e2e.external.schema.schema_e2e.options;


import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class OutputOption {

  private static final Logger LOG = LoggerFactory.getLogger(OutputOption.class);

  @Parameter(names = "--output", description = "Output file to write the created id id")
  private String path;

  public void write(String output) throws FileNotFoundException {
    if (path == null) {
      LOG.warn("Skipping writing id file - no output file specified!");
      return;
    }
    try (PrintWriter out = new PrintWriter(path)) {
      out.println(output);
    }
  }
}
