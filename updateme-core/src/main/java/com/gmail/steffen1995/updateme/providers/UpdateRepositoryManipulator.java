package com.gmail.steffen1995.updateme.providers;

import java.io.File;
import java.util.List;

/**
 * A base for classes that implement behaviour for manipulating the directory structure of updates.
 * @author Steffen Schoen
 */
public interface UpdateRepositoryManipulator {
  /**
   * The name of the default deployment channel.
   */
  String DEFAULT_CHANNEL = "stable";

  /**
   * Creates a new update channel.
   * @param channelName the name of the channel
   */
  void createChannel(String channelName, ProgressChangedListener progress) throws UpdateRepositoryException;

  /**
   * Initializes the basic structure.
   */
  void initStructure(ProgressChangedListener progress) throws UpdateRepositoryException;

  /**
   * Adds a new update package to the repository.
   * @param updatePackage the update package
   * @param channel the name of the deployment channel
   */
  void pushUpdate(File updatePackage, String channel, ProgressChangedListener progress) throws UpdateRepositoryException;

  /**
   * Get a list of all available channels.
   * @return a list of all available channel names
   */
  List<String> availableChannels(ProgressChangedListener progress) throws UpdateRepositoryException;

  /**
   * Gets all update info files.
   * @param channel the channel to look for files
   * @return a list of update info files
   */
  List<File> updateInfoFiles(String channel, ProgressChangedListener progress) throws UpdateRepositoryException;

  /**
   * Gets the update package for the specified version.
   * @param version the version of the update
   * @param channel the deployment channel of the update
   * @return the update package file
   */
  File pullUpdate(String version, String channel, ProgressChangedListener progress) throws UpdateRepositoryException;
}
