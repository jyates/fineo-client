package io.fineo.client;

import java.util.UUID;
import java.util.function.Function;

/**
 * Define non-body parameter for a method operation
 */
public @interface Parameter {

  String name() default "";

  Type type() default Type.QUERY;

  Strategy nullStrategy() default Strategy.ERROR;

  enum Type {
    HEADER,
    QUERY,
    PATH;
  }

  enum Strategy {
    RANDOM((param) -> UUID.randomUUID().toString()),
    ERROR((param) -> {
      throw new RuntimeException("Parameter " + param.name() + " cannot be null!");
    });

    public final Function<Parameter, String> onNull;

    Strategy(Function<Parameter, String> onNull) {
      this.onNull = onNull;
    }
  }
}
