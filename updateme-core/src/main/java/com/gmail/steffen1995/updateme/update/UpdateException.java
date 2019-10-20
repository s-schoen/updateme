package com.gmail.steffen1995.updateme.update;

import lombok.Getter;

/**
 * An {@link Exception} that occurs in the context of updates.
 * @author Steffen Schoen
 */
public class UpdateException extends Exception {
  /**
   * {@inheritDoc}
   */
  public UpdateException(String message) {
    super(message);
  }

  /**
   * {@inheritDoc}
   */
  public UpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
