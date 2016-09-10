package io.fineo.e2e.external.schema.options;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MetricOption {

  @Parameter(names = "--metric", description = "User name of the metric")
  public List<String> metricNames = new ArrayList<>();

  @Parameter(names = "--aliases", variableArity = true,
             description = "Space separated names of <metric>.alias")
  public List<String> metricAliases = new ArrayList<>();

}
