package com.gmail.steffen1995.updateme.config;

import com.gmail.steffen1995.updateme.providers.UpdateRepositoryManipulator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Represents the update configuration.
 * @author Steffen Schön
 */
public class UpdateConfig {
  @Getter
  @Setter(AccessLevel.PROTECTED)
  private UpdateRepositoryManipulator repository;
  private List<UpdateConfigExtension> extensions;

  @Getter(AccessLevel.PACKAGE)
  private Map<String, Callable<UpdateRepositoryManipulator>> repositoryTypeInitializer;

  /**
   * Constructor.
   */
  public UpdateConfig() {
    extensions = new ArrayList<>();
    repositoryTypeInitializer = new HashMap<>();
  }

  /**
   * Gets the extension with the specified type.
   * @param type the type of the extension to get
   * @param <T> the type of the extension to get
   * @return the extension or {@code null} if the extension is not registered
   */
  public <T extends UpdateConfigExtension> T getExtension(Class<T> type) {
    for (UpdateConfigExtension ext: extensions) {
      if (ext.getClass().equals(type)) {
        return type.cast(ext);
      }
    }

    return null;
  }

  /**
   * Registers an extension to the config.
   * @param type the type o f extension to register
   * @return {@code true} if the registration was successful, {@code false} otherwise
   */
  public boolean registerExtension(Class<? extends UpdateConfigExtension> type) {
    try {
      UpdateConfigExtension instance = type.newInstance();
      this.extensions.add(instance);
      return true;
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void registerRepositoryType(String identifier, Callable<UpdateRepositoryManipulator> createInstance) {
    this.repositoryTypeInitializer.put(identifier, createInstance);
  }

  public List<UpdateConfigExtension> getExtensions() {
    return Collections.unmodifiableList(extensions);
  }
}
