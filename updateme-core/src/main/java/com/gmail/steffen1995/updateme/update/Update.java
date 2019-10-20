package com.gmail.steffen1995.updateme.update;

import com.gmail.steffen1995.updateme.util.HashCalculator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The representation of an application update.
 * @author Steffen Schoen
 */
@EqualsAndHashCode @Getter
public class Update {
  @Setter(AccessLevel.PRIVATE)
  private String version;
  private List<UpdateObject> updateObjects;
  @Setter(AccessLevel.PRIVATE)
  private Date publishDate;

  /**
   * Constructor.
   * @param version the version of this update
   */
  public Update(String version) {
    this.version = Objects.requireNonNull(version);
    // Publish date = current date
    this.publishDate = new Date();

    updateObjects = new ArrayList<>();
  }

  /**
   * Packages an update, i.e. all files associated with the update, including an update info into a compressed file.
   * @param filePath The path to the compressed update file
   * @throws IOException when something went wrong while packaging the update
   */
  public void pack(String filePath) throws IOException {
    // create temp directory
    Path tmpDir = Files.createTempDirectory(version);

    // create update info file
    UpdateInfo updateInfo = UpdateInfo.fromUpdate(this);
    updateInfo.writeToFile(Paths.get(tmpDir.toString(), "updateInfo.json").toString());

    // zip files
    try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(filePath))) {
      // updateInfo.json
      try (FileInputStream fis = new FileInputStream(Paths.get(tmpDir.toString(), "updateInfo.json").toString())) {
        zipOut.putNextEntry(new ZipEntry("updateInfo.json"));

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) >= 0) {
          zipOut.write(buffer, 0, length);
        }
      }

      for (UpdateObject uo: updateObjects) {
        try (FileInputStream fis = new FileInputStream(Paths.get(uo.getFile().getAbsolutePath()).toString())) {
          zipOut.putNextEntry(new ZipEntry(uo.getFile().getName()));

          byte[] buffer = new byte[1024];
          int length;
          while ((length = fis.read(buffer)) >= 0) {
            zipOut.write(buffer, 0, length);
          }
        }
      }
    }
  }

  /**
   * Unpacks an update from a compressed update file.
   * @param updateFile the compressed update to unpack
   * @return the unpacked update
   * @throws IOException when something went wrong while unpacking the update
   * @throws UpdateException when there is a problem with the update
   */
  public static Update unpack(File updateFile) throws IOException, UpdateException {
    Update update = new Update("");

    // create temp directory
    Path tmpDir = Files.createTempDirectory("update-");

    // unzip all files
    byte[] buffer = new byte[1024];
    try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(updateFile))) {
      ZipEntry entry = zipIn.getNextEntry();
      while (entry != null) {
        File exportedFile = new File(tmpDir.toString(), entry.getName());
        try (FileOutputStream fos = new FileOutputStream(exportedFile)) {
          int length;
          while ((length = zipIn.read(buffer)) >= 0) {
            fos.write(buffer, 0, length);
          }
        }

        entry = zipIn.getNextEntry();
      }
    }

    // read updateInfo
    Path udpateInfoPath = Paths.get(tmpDir.toString(), "updateInfo.json");
    if (!Files.exists(udpateInfoPath)) {
      throw new UpdateException("updateInfo.json missing");
    }

    UpdateInfo updateInfo = UpdateInfo.readFromFile(udpateInfoPath.toString());
    update.setVersion(updateInfo.getVersion());
    update.setPublishDate(updateInfo.getPublishDate());

    // check file hashes
    for (UpdateInfo.FileUpdate fu : updateInfo.getFileUpdates()) {
      Path filePath = Paths.get(tmpDir.toString(), fu.getLocalPath());

      if (!Files.exists(filePath)) {
        throw new UpdateException("File '" + fu.getLocalPath() + "' does not exist");
      }

      String updateChecksum = HashCalculator.sha256(new File(filePath.toString()));

      if (!updateChecksum.equals(fu.getChecksum())) {
        throw new UpdateException("Checksum for file '" + fu.getLocalPath() + "' does not match");
      }
    }

    // add update objects to update
    Files.list(tmpDir).filter(path -> !path.getFileName().toString().equals("updateInfo.json")).forEach(f -> {
      update.getUpdateObjects().add(new UpdateObject(f.toString(), "/"));
    });

    // calculate checksums for update objects
    for (UpdateObject updateObject :update.getUpdateObjects()) {
      updateObject.calculateChecksum();
    }

    return update;
  }

  /**
   * Unpacks an update from a compressed update file.
   * @param updateFile the compressed update to unpack
   * @return the unpacked update
   * @throws IOException when something went wrong while unpacking the update
   * @throws UpdateException when there is a problem with the update
   */
  public static Update unpack(String updateFile) throws IOException, UpdateException {
    return unpack(new File(updateFile));
  }
}
