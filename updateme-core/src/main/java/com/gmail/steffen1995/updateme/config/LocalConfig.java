package com.gmail.steffen1995.updateme.config;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for local repositories.
 * @author Steffen Schön
 */
@Getter
public class LocalConfig implements UpdateConfigExtension {
  private List<ConfigProperty> configValues;

  private static final ConfigProperty<String> BASE_DIR = ConfigProperty.string("local.baseDir");

  public LocalConfig() {
    configValues = new ArrayList<>();
    configValues.add(BASE_DIR);
  }

  public String getBaseDirectory() {
    return BASE_DIR.getValue();
  }

  @Override
  public List<ConfigProperty> getProperties() {
    return configValues;
  }
}
