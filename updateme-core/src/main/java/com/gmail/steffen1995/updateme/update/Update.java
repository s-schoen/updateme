package com.gmail.steffen1995.updateme.update;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The representation of an application update.
 * @author Steffen Schoen
 */
@EqualsAndHashCode @Getter
public class Update {
  private String version;
  private List<UpdateObject> updateObjects;
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
}
