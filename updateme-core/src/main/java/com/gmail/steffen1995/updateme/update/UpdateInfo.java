package com.gmail.steffen1995.updateme.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the info of an update.
 * @author Steffen Schoen
 */
@EqualsAndHashCode
@Getter
@Setter
public class UpdateInfo {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

  private String version;
  private Date publishDate;
  private List<FileUpdate> fileUpdates;

  /**
   * Constructor.
   * @param version the version of the update
   * @param publishDate the date when the update was published
   */
  public UpdateInfo(String version, Date publishDate) {
    this.version = version;
    this.publishDate = publishDate;

    fileUpdates = new ArrayList<>();
  }

  /**
   * Writes the {@link UpdateInfo} to a file.
   * @param pathToFile the path to the info file
   * @throws IOException when the {@link UpdateInfo} cannot be written to the file
   */
  public void writeToFile(String pathToFile) throws IOException {
    JSONObject object = new JSONObject();
    object.put("version", version);
    object.put("publishDate", DATE_FORMAT.format(publishDate));

    // add update files
    JSONArray updateObjectsList = new JSONArray();

    for (FileUpdate obj : fileUpdates) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("path", obj.getLocalPath());
      jsonObject.put("sha256", obj.getChecksum());
      jsonObject.put("size", obj.getSizeBytes());

      updateObjectsList.put(jsonObject);
    }

    object.put("files", updateObjectsList);

    Files.write(Paths.get(pathToFile), object.toString().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Parses an {@link UpdateInfo} object from a file.
   * @param pathToFile the file to read
   * @return the parsed {@link UpdateInfo}
   * @throws IOException when the file cannot be read or the file cannot be parsed
   */
  public static UpdateInfo readFromFile(String pathToFile) throws IOException {
    String json = new String(Files.readAllBytes(Paths.get(pathToFile)), StandardCharsets.UTF_8);
    JSONObject rootObject = new JSONObject(json);

    try {
      Date publishDate = DATE_FORMAT.parse(rootObject.getString("publishDate"));
      UpdateInfo info = new UpdateInfo(rootObject.getString("version"), publishDate);

      JSONArray fileUpdatesJson = rootObject.getJSONArray("files");

      for (int i = 0; i < fileUpdatesJson.length(); i++) {
        JSONObject fileUpdate = fileUpdatesJson.getJSONObject(i);

        info.getFileUpdates().add(FileUpdate.builder()
                .localPath(fileUpdate.getString("path"))
                .checksum(fileUpdate.getString("sha256"))
                .sizeBytes(fileUpdate.getLong("size")).build()
        );
      }

      return info;
    } catch (ParseException e) {
      throw new IOException("Could not parse publish date from file", e);
    }
  }

  /**
   * Parses an {@link UpdateInfo} object from a file.
   * @param infoFile the file to parse
   * @return the parsed {@link UpdateInfo}
   * @throws IOException when the file cannot be read or the file cannot be parsed
   */
  public static UpdateInfo readFromFile(File infoFile) throws IOException {
    return readFromFile(infoFile.getAbsolutePath());
  }

  /**
   * Extracts the update info from am {@link Update}.
   * @param update the update that sources the info
   * @return the {@link UpdateInfo} associated with the given {@link Update}
   */
  public static UpdateInfo fromUpdate(Update update) {
    UpdateInfo info = new UpdateInfo(update.getVersion(), update.getPublishDate());

    for (UpdateObject obj : update.getUpdateObjects()) {
      info.getFileUpdates().add(FileUpdate.builder()
              .localPath(obj.getBasePath() + obj.getFile().getName())
              .checksum(obj.getChecksum())
              .sizeBytes(obj.getFile().length()).build()
      );
    }

    return info;
  }

  /**
   * Representation of an update file.
   */
  @Value
  @Builder
  public static class FileUpdate {
    private String localPath;
    private String checksum;
    private long sizeBytes;
  }
}
