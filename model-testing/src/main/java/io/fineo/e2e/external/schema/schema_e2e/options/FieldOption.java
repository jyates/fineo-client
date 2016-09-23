package io.fineo.e2e.external.schema.schema_e2e.options;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FieldOption {

  @Parameter(names = "--field",
             description = "<metric>.field name. Can specify multiple times")
  public List<String> fieldName = new ArrayList<>();

  @Parameter(names = "--faliases",
             description = "Names of <metric>.<field>.alias. Can specify multiple times")
  public List<String> fieldAliases = new ArrayList<>();

  @Parameter(names = "--type",
             description = "<metric>.<field>.type. Can specify multiple times")
  public List<String> fieldType = new ArrayList<>();
}
