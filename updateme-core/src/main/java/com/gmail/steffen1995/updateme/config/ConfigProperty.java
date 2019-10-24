package com.gmail.steffen1995.updateme.config;

import lombok.Getter;

/**
 * Representation of a config value.
 * @param <T> the type of the config value
 * @author Steffen Schön
 */
@Getter
public abstract class ConfigProperty<T> {
  private T value;
  private String key;

  public ConfigProperty(String key) {
    this.key = key;
  }

  void parseString(String valueString) {
    this.value = fromString(valueString);
  }

  protected abstract T fromString(String valueString);

  // for primitive types

  public static ConfigProperty<String> string(String key) {
    return new ConfigProperty<String>(key) {
      @Override
      protected String fromString(String valueString) {
        return valueString;
      }
    };
  }

  public static ConfigProperty<Integer> integer(String key) {
    return new ConfigProperty<Integer>(key) {
      @Override
      protected Integer fromString(String valueString) {
        return Integer.parseInt(valueString);
      }
    };
  }
}
