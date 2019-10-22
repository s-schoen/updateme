package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manipulates a update directory structure on the local file system.
 * @author Steffen Schoen
 */
public class LocalUpdateRepository implements UpdateRepositoryManipulator {
  private File baseDirectory;

  public LocalUpdateRepository(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public LocalUpdateRepository(String baseDirectory) {
    this(new File(baseDirectory));
  }

  @Override
  public void createChannel(String channelName) throws UpdateRepositoryException {
    checkChannelExistance(channelName, true);

    // create channel directory
    try {
      Files.createDirectory(Paths.get(baseDirectory.getAbsolutePath(), channelName));
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not create new channel", e);
    }
  }

  @Override
  public void initStructure() throws UpdateRepositoryException {
    // create default channel
    try {
      Files.createDirectory(Paths.get(baseDirectory.getAbsolutePath(), UpdateRepositoryManipulator.DEFAULT_CHANNEL));
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not create default channel", e);
    }
  }

  @Override
  public void pushUpdate(File updatePackage, String channel) throws UpdateRepositoryException {
    checkChannelExistance(channel, false);

    Path channelPath = Paths.get(baseDirectory.getAbsolutePath(), channel);
    Update update;
    try {
      update = Update.unpack(updatePackage);
    } catch (Exception e) {
      throw new UpdateRepositoryException("Could not parse update package", e);
    }
    UpdateInfo updateInfo = UpdateInfo.fromUpdate(update);
    Path versionPath = Paths.get(channelPath.toString(), updateInfo.getVersion());

    try {
      // create directory for version
      Files.createDirectory(versionPath);

      // write update info to directory
      updateInfo.writeToFile(Paths.get(versionPath.toString(), "updateInfo.json").toString());

      // copy update package
      Files.copy(Paths.get(updatePackage.getAbsolutePath()),
              Paths.get(versionPath.toString(), "data.zip"),
              StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not push update package", e);
    }

  }

  @Override
  public List<String> availableChannels() throws UpdateRepositoryException {
    try {
      List<Object> collect = Files
              .list(Paths.get(baseDirectory.getAbsolutePath()))
              .filter(f -> Files.isDirectory(f))
              .map((Function<Path, Object>) Path::getFileName)
              .collect(Collectors.toList());

      return collect.stream().map(Object::toString).collect(Collectors.toList());
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not list available channels", e);
    }
  }

  @Override
  public List<File> updateInfoFiles(String channel) throws UpdateRepositoryException {
    checkChannelExistance(channel, false);

    List<File> updateInfoFiles = new ArrayList<>();

    try {
      Files.walkFileTree(Paths.get(baseDirectory.toString(), channel), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (file.getFileName().toString().equals("updateInfo.json")) {
            updateInfoFiles.add(file.toFile());
          }

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not fetch update infos", e);
    }

    return updateInfoFiles;
  }

  @Override
  public File pullUpdate(String version, String channel) throws UpdateRepositoryException {
    checkChannelExistance(channel, false);
    Path channelPath = Paths.get(baseDirectory.getAbsolutePath(), channel);

    if (!Files.exists(Paths.get(channelPath.toString(), version))) {
      // version does not exist
      throw new UpdateRepositoryException("The requested version does not exist");
    }

    Path updatePackagePath = Paths.get(channelPath.toString(), version, "data.zip");
    if (!Files.exists(updatePackagePath)) {
      throw new UpdateRepositoryException("Update data not available");
    }

    return updatePackagePath.toFile();
  }

  private void checkChannelExistance(String channelName, boolean throwWhenExists) throws UpdateRepositoryException {
    try {
      boolean channelExists = Files
          .list(Paths.get(baseDirectory.getAbsolutePath()))
          .anyMatch(f -> f.getFileName().toString().equals(channelName));

      if (channelExists && throwWhenExists) {
        throw new UpdateRepositoryException("Channel already exists");
      } else if (!channelExists && !throwWhenExists) {
        throw new UpdateRepositoryException("Channel does not exist");
      }
    } catch (IOException e) {
      throw new UpdateRepositoryException("Could not access channels", e);
    }
  }
}
