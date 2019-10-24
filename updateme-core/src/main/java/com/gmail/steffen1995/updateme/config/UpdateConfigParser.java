package com.gmail.steffen1995.updateme.config;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Parses a {@link UpdateConfig} from a .properties file.
 * @author Steffen Schön
 */
@Getter
public class UpdateConfigParser {
  private UpdateConfig config;
  @Setter
  private File propertiesFile;

  /**
   * Constructor.
   * @param propertiesFile the properties file to parse
   */
  public UpdateConfigParser(File propertiesFile) {
    this.propertiesFile = propertiesFile;
    this.config = new UpdateConfig();
  }

  /**
   * Constructor.
   * @param propertiesFile the path to the properties file
   */
  public UpdateConfigParser(String propertiesFile) {
    this(new File(propertiesFile));
  }

  /**
   * Parses the properties file and sets the appropriate values in the config.
   * @throws Exception when the properties file could not be parsed
   */
  void parse() throws Exception {
    Properties properties = new Properties();

    try (InputStream inputStream = new FileInputStream(propertiesFile)) {
      properties.load(inputStream);

      // try to get all properties from file
      for (UpdateConfigExtension ext: config.getExtensions()) {
        for (ConfigProperty val: ext.getProperties()) {
          String fileValue = properties.getProperty(val.getKey());
          if (fileValue != null) {
            val.parseString(fileValue);
          }
        }
      }

      // repository type
      String repoType = properties.getProperty("repository.type");
      if (config.getRepositoryTypeInitializer().containsKey(repoType)) {
        // instantiate the repository
        config.setRepository(config.getRepositoryTypeInitializer().get(repoType).call());
      }
    }
  }
}
