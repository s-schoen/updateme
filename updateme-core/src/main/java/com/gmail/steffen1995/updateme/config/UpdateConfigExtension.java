package com.gmail.steffen1995.updateme.config;

import java.util.List;

/**
 * Common interface for extensions.
 * @author Steffen Schön
 */
public interface UpdateConfigExtension {
  /**
   * Gets all properties that are associated with this extension.
   * @return all registered properties
   */
  List<ConfigProperty> getProperties();
}
