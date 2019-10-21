package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateException;
import com.gmail.steffen1995.updateme.update.UpdateInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Base class for update providers.
 * @author Steffen Schoen
 */
public abstract class UpdateProvider {
  private List<UpdateFetchingProgressChangedListener> updateProgressChangedListeners;
  private List<UpdateFetchingProgressChangedListener> availableUpdateProgressChangedListeners;

  /**
   * Constructor.
   */
  public UpdateProvider() {
    this.updateProgressChangedListeners = new ArrayList<>();
    this.availableUpdateProgressChangedListeners = new ArrayList<>();
  }

  /**
   * Registers a listener for the OnUpdateFetchingProgressChanged event. The event is fired when
   * the progress of fetching an {@link Update} changed after calling
   * {@link UpdateProvider#fetchUpdate(String)} or {@link UpdateProvider#fetchUpdate(UpdateInfo)}.
   * @param listener the listener that will be invoked
   */
  public void addOnUpdateFetchingProgressChanged(UpdateFetchingProgressChangedListener listener) {
    this.updateProgressChangedListeners.add(listener);
  }

  /**
   * Registers a listener to the OnAvailableUpdateFetchingProgressChanged event.
   * The event is fired when the progress of fetching all available updates changed, i.e. after
   * calling {@link UpdateProvider#fetchAvailableUpdates(String)}.
   * @param listener the listener to invoke
   */
  public void addOnAvailableUpdateFetchingProgressChanged(UpdateFetchingProgressChangedListener listener) {
    this.availableUpdateProgressChangedListeners.add(listener);
  }

  /**
   * Fires the OnUpdateFetchingProgressChanged event.
   * @param current current progress
   * @param total total progress
   */
  protected void fireOnFetchingProgressChanged(long current, long total) {
    for (UpdateFetchingProgressChangedListener l: updateProgressChangedListeners) {
      if (l != null) {
        l.onChange(current, total);
      }
    }
  }

  /**
   * Fires the OnAvailableUpdateFetchingProgressChanged event.
   * @param current current progress
   * @param total total progress
   */
  protected void fireOnAvailableFetchingProgressChanged(long current, long total) {
    for (UpdateFetchingProgressChangedListener l: availableUpdateProgressChangedListeners) {
      if (l != null) {
        l.onChange(current, total);
      }
    }
  }

  /**
   * Fetches the {@link Update} that corresponds with the given {@link UpdateInfo}.
   * @param updateToFetch the update to fetch
   * @return the fetched {@link Update}
   * @throws IOException when the update package could not be read.
   * @throws UpdateException when an error occurred during the update process.
   */
  public Future<Update> fetchUpdate(UpdateInfo updateToFetch) throws IOException, UpdateException {
    return fetchUpdate(updateToFetch.getVersion());
  }

  /**
   * Fetches the {@link Update} with the given version.
   * @param version the version to fetch
   * @return the fetched {@link Update}
   * @throws IOException when the update package could not be read.
   * @throws UpdateException when an error occurred during the update process.
   */
  public abstract Future<Update> fetchUpdate(String version) throws IOException, UpdateException;

  /**
   * Fetch all available updates.
   * @param channel the deployment channel to fetch the updates from
   * @return a list of {@link UpdateInfo} of all available updates in the specified channel
   * @throws IOException when the update package could not be read.
   * @throws UpdateException when an error occurred during the update process.
   */
  public abstract Future<List<UpdateInfo>> fetchAvailableUpdates(String channel) throws IOException, UpdateException;

  @FunctionalInterface
  public interface UpdateFetchingProgressChangedListener {
    /**
     * Invoken when the progress of fetching an update is changed.
     * @param currentProgress the current fetching progress
     * @param total the total progress needed to complete the update fetching
     */
    void onChange(long currentProgress, long total);
  }
}
