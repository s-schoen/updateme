package com.gmail.steffen1995.updateme.config;

import com.gmail.steffen1995.updateme.providers.UpdateRepositoryManipulator;
import com.gmail.steffen1995.updateme.util.MockingUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * test for com.gmail.steffen1995.updateme.config.UpdateConfigParser class
 *
 * @author Steffen Schoen
 **/
public class UpdateConfigParserTest {
  private UpdateConfigParser parser;
  private final UpdateRepositoryManipulator repo = mock(UpdateRepositoryManipulator.class);

  @Before
  public void beforeTest() throws Exception {
    parser = new UpdateConfigParser(UpdateConfigParserTest.class.getResource("/update.properties").getPath());

    parser.getConfig().registerExtension(MockingUtils.MockedUpdateConfigExt.class);

    parser.getConfig().registerRepositoryType("test", () -> repo);

    parser.parse();
  }

  @Test
  public void getValues() {
    MockingUtils.MockedUpdateConfigExt ext = parser.getConfig().getExtension(MockingUtils.MockedUpdateConfigExt.class);

    assertNotNull(ext);
    assertEquals("1", ext.getVal1());
    assertEquals(1, ext.getVal2());
  }

  @Test
  public void repoInstantiation() {
    assertEquals(repo, parser.getConfig().getRepository());
  }

  @Test
  public void repoInstantiationPropertyAccess() throws Exception {
    parser.getConfig().getRepositoryTypeInitializer().clear();

    parser.getConfig().registerRepositoryType("test", () -> {
      assertEquals("1", parser.getConfig().getExtension(MockingUtils.MockedUpdateConfigExt.class).getVal1());
      assertEquals(1, parser.getConfig().getExtension(MockingUtils.MockedUpdateConfigExt.class).getVal2());

      return null;
    });

    parser.parse();
  }
}