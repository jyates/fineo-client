package io.fineo.client;

/**
 *
 */
public @interface Parameter {

  String name() default "";

  Type type() default Type.QUERY;

  String value() default "";

  public enum Type{
    HEADER,
    QUERY;
  }
}