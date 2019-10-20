package com.gmail.steffen1995.updateme.update;

/**
 * An {@link Exception} that occurs in the context of updates.
 * @author Steffen Schoen
 */
public class UpdateException extends Exception {
  public UpdateException(String message) {
    super(message);
  }

  public UpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
