package com.gmail.steffen1995.updateme.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

      return bytesToHex(digest.digest());
    }
  }

  /**
   * Converts a byte array into its hexadecimal representation.
   * @param bytes the bytes to convert
   * @return a string that contains the hexadecimal representation of the byte array
   */
  private static String bytesToHex(byte[] bytes) {
    char[] hexArray = "0123456789abcdef".toCharArray();

    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }
}
