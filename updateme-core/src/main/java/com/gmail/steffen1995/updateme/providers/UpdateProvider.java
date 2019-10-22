package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateInfo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * Base class for update providers.
 * @author Steffen Schoen
 */
public class UpdateProvider {
  private List<UpdateFetchingProgressChangedListener> updateProgressChangedListeners;
  private List<UpdateFetchingProgressChangedListener> availableUpdateProgressChangedListeners;

  private UpdateRepositoryManipulator repository;
  private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

  /**
   * Constructor.
   * @param repository The provider for update repository access that will be used
   */
  public UpdateProvider(UpdateRepositoryManipulator repository) {
    this.repository = Objects.requireNonNull(repository);

    this.updateProgressChangedListeners = new ArrayList<>();
    this.availableUpdateProgressChangedListeners = new ArrayList<>();
  }

  /**
   * Registers a listener for the OnUpdateFetchingProgressChanged event. The event is fired when
   * the progress of fetching an {@link Update} changed after calling
   * {@link UpdateProvider#fetchUpdate(String, String)} or
   * {@link UpdateProvider#fetchUpdate(UpdateInfo, String)}.
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
   * @param channel the deployment channel to fetch the update from
   * @return the fetched {@link Update}
   */
  public ListenableFuture<Update> fetchUpdate(UpdateInfo updateToFetch, String channel) {
    return fetchUpdate(updateToFetch.getVersion(), channel);
  }

  /**
   * Fetches the {@link Update} with the given version.
   * @param version the version to fetch
   * @param channel the deployment channel to fetch the update from
   * @return the fetched {@link Update}
   */
  public ListenableFuture<Update> fetchUpdate(String version, String channel) {
    return executorService.submit(() -> {
      File updatePackage = repository.pullUpdate(version, channel);

      return Update.unpack(updatePackage);
    });
  }

  /**
   * Fetch all available updates.
   * @param channel the deployment channel to fetch the updates from
   * @return a list of {@link UpdateInfo} of all available updates in the specified channel
   */
  public ListenableFuture<List<UpdateInfo>> fetchAvailableUpdates(String channel) {
    return executorService.submit(() -> {
      List<UpdateInfo> updateInfos = new ArrayList<>();
      List<File> updateInfoFiles = repository.updateInfoFiles(channel);

      for (File f: updateInfoFiles) {
        updateInfos.add(UpdateInfo.readFromFile(f));
      }

      return updateInfos;
    });
  }

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
