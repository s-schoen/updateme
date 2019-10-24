package com.gmail.steffen1995.updateme.providers;

/**
 * A listener interface for events that are fired when the progress of an operation changes.
 * @author Steffen Schön
 */
@FunctionalInterface
public interface ProgressChangedListener {
  /**
   * Fires a the given {@code listener} after performing null checks.
   * @param listener the listener to fire
   * @param current the current progress
   * @param total the total progress
   */
  static void fire(ProgressChangedListener listener, long current, long total) {
    if (listener != null) {
      listener.onChange(current, total);
    }
  }

  /**
   * Fires a complete change on the {@code listener}, i.e. the current progress equals the total
   * progress
   * @param listener the listener to fire
   */
  static void fireComplete(ProgressChangedListener listener) {
    fire(listener, 100, 100);
  }

  /**
   * Invoked when the progress of fetching an update is changed.
   * @param currentProgress the current fetching progress
   * @param total the total progress needed to complete the update fetching
   */
  void onChange(long currentProgress, long total);
}
