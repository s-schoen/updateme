package com.gmail.steffen1995.updateme.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for calculation checksums.
 * @author Steffen Schoen
 */
public class HashCalculator {

  /**
   * Calculates the SHA-256 hash of the given file and encodes it in Base64.
   * @param file the file to hash
   * @return the Base64 encoded hash
   * @throws IOException when the file cannot be read
   */
  @SneakyThrows(NoSuchAlgorithmException.class)
  public static String sha256(File file) throws IOException {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("The file must not be a directory");
    }

    try (InputStream inputStream = new FileInputStream(file)) {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        digest.update(buffer, 0, length);
      }

      return Base64.getEncoder().encodeToString(digest.digest());
    }
  }
}
