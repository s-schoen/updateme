package com.gmail.steffen1995.updateme.providers;

/**
 * An exception that occurs while manipulating an update repository.
 * @author Steffen Schoen
 */
public class UpdateRepositoryException extends Exception {
  public UpdateRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public UpdateRepositoryException(String message) {
    super(message);
  }
}
