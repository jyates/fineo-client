package io.fineo.e2e.external.write.stream;

import com.beust.jcommander.Parameter;

/**
 *
 */
public class SequentialEvents {

  @Parameter(names = "--seq", description = "If the events should be sent sequentially (/event) "
                                            + "or as a batch (/events). By default sent as a batch")
  public boolean sequential = false;
}
