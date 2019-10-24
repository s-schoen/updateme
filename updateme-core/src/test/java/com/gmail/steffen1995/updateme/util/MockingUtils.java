package com.gmail.steffen1995.updateme.util;

import com.gmail.steffen1995.updateme.config.ConfigProperty;
import com.gmail.steffen1995.updateme.config.UpdateConfigExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Steffen Schön
 */
public class MockingUtils {
  public static class MockedUpdateConfigExt implements UpdateConfigExtension {
    private static final ConfigProperty<String> VAL_1 = ConfigProperty.string("test.val1");
    private static final ConfigProperty<Integer> VAL_2 = ConfigProperty.integer("test.val2");

    private List<ConfigProperty> properties;

    public MockedUpdateConfigExt() {
      properties = new ArrayList<>();
      properties.add(VAL_1);
      properties.add(VAL_2);
    }

    @Override
    public List<ConfigProperty> getProperties() {
      return properties;
    }

    public String getVal1() {
      return VAL_1.getValue();
    }

    public int getVal2() {
      return VAL_2.getValue();
    }
  }
}
