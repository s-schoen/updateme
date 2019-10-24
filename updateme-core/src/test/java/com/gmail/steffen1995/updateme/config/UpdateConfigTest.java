package com.gmail.steffen1995.updateme.config;

import com.gmail.steffen1995.updateme.util.MockingUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.MockUtil;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
/**
 * @author Steffen Schön
 */

/**
 * test for com.gmail.steffen1995.updateme.config.UpdateConfig class
 *
 * @author Steffen Schoen
 **/
public class UpdateConfigTest {
  private UpdateConfig updateConfig;

  @Before
  public void beforeTest() {
    updateConfig = new UpdateConfig();
  }

  @Test
  public void extensionRegistration() {
    assertEquals(0, updateConfig.getExtensions().size());
    assertTrue(updateConfig.registerExtension(MockingUtils.MockedUpdateConfigExt.class));
    UpdateConfigExtension ext = updateConfig.getExtension(MockingUtils.MockedUpdateConfigExt.class);
    assertEquals(ext.getClass(), MockingUtils.MockedUpdateConfigExt.class);
    assertEquals(1, updateConfig.getExtensions().size());

  }

  @Test
  public void extensionNotRegistered() {
    assertNull(updateConfig.getExtension(MockingUtils.MockedUpdateConfigExt.class));
  }

  @Test
  public void registerExtensionIllegalAccess() {
    assertFalse(updateConfig.registerExtension(((UpdateConfigExtension) () -> null).getClass()));
  }
}