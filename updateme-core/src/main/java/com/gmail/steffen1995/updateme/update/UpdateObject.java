package com.gmail.steffen1995.updateme.update;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.gmail.steffen1995.updateme.util.HashCalculator;
import lombok.EqualsAndHashCode;
import lombok.Getter;


/**
 * A representation of an object that is relevant for updates.
 * @author Steffen Schoen
 */
@EqualsAndHashCode @Getter
public class UpdateObject {
  private File file;
  private String basePath;
  private String checksum;

  /**
   * Constructor.
   * @param file the file that is required to be updated
   * @param basePath the path of this file relative to the application root
   */
  public UpdateObject(File file, String basePath) {
    this.file = Objects.requireNonNull(file);
    this.basePath = Objects.requireNonNull(basePath);
  }

  /**
   * Constructor.
   * @param pathToFile the path to the file that is required to be updated
   * @param basePath the path of this file relative to the application root
   */
  public UpdateObject(String pathToFile, String basePath) {
    this(new File(Objects.requireNonNull(pathToFile)), basePath);
  }

  /**
   * Calculates the SHA-256 checksum of the file.
   * @throws IOException when the file cannot be opened
   */
  public void calculateChecksum() throws IOException {
    checksum = HashCalculator.sha256(file);
  }

  /**
   * Creates a set of {@link UpdateObject} from all files in the given directory.
   * @param directory the directory to walk through
   * @param basePath the path of this file relative to the application root
   * @return a list of {@link UpdateObject}
   * @throws IOException when the files in the directory cannot be listed
   */
  public static List<UpdateObject> fromDirectory(File directory, String basePath) throws IOException {
    return fromDirectory(directory.getAbsolutePath(), basePath);
  }

  /**
   * Creates a set of {@link UpdateObject} from all files in the given directory.
   * @param pathToDirectory the path to the directory
   * @param basePath the path of this file relative to the application root
   * @return a list of {@link UpdateObject}
   * @throws IOException when the files in the directory cannot be listed
   */
  public static List<UpdateObject> fromDirectory(String pathToDirectory, String basePath) throws IOException {
    Objects.requireNonNull(pathToDirectory);
    List<UpdateObject> updateObjects = new ArrayList<>();

    Files
            .walk(Paths.get(pathToDirectory))
            .filter(Files::isRegularFile)
            .forEach(f -> {
              updateObjects.add(new UpdateObject(f.toFile(), basePath));
            });

    return updateObjects;
  }
}
