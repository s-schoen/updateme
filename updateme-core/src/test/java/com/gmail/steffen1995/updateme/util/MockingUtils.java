package com.gmail.steffen1995.updateme.util;

import com.gmail.steffen1995.updateme.config.ConfigProperty;
import com.gmail.steffen1995.updateme.config.UpdateConfigExtension;

import java.util.List;

/**
 * @author Steffen Schön
 */
public class MockingUtils {
  public static class MockedUpdateConfigExt implements UpdateConfigExtension {

    @Override
    public List<ConfigProperty> getProperties() {
      return null;
    }
  }
}
