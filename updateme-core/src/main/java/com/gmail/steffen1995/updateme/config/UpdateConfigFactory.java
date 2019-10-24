package com.gmail.steffen1995.updateme.config;

import com.gmail.steffen1995.updateme.providers.LocalUpdateRepository;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

/**
 * Factory for parsing and managing a global {@link UpdateConfig} instance.
 * @author Steffen Schön
 */
@Slf4j
public class UpdateConfigFactory {
  private static final String DEFAULT_UPDATE_PROPERTIES =
          Paths.get(System.getProperty("user.dir"), "update.properties").toString();

  private static UpdateConfigFactory INSTANCE;
  private UpdateConfigParser parser;

  /**
   * Private constructor.
   */
  private UpdateConfigFactory() {
    log.info("Loading update properties from {}", DEFAULT_UPDATE_PROPERTIES);
    parser = new UpdateConfigParser(DEFAULT_UPDATE_PROPERTIES);

    // register config extensions
    parser.getConfig().registerExtension(LocalConfig.class);

    // register repo instantiation
    parser.getConfig().registerRepositoryType("local", () -> {
      String dir = parser.getConfig().getExtension(LocalConfig.class).getBaseDirectory();
      return new LocalUpdateRepository(dir);
    });

    try {
      parser.parse();
    } catch (Exception e) {
      log.error("Could not parse update properties file");
    }
  }

  private static UpdateConfigFactory getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new UpdateConfigFactory();
    }

    return INSTANCE;
  }

  /**
   * Getter for a global instance of a {@link UpdateConfig} that is read from the update.properties
   * file in the working directory.
   * @return a global {@link UpdateConfig} instance
   */
  public static UpdateConfig getConfig() {
    return getInstance().parser.getConfig();
  }
}
