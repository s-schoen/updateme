package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateException;
import com.gmail.steffen1995.updateme.update.UpdateInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base class for update providers.
 * @author Steffen Schoen
 */
public class UpdateProvider {
  private ProgressChangedListener updateProgressChangedListener;
  private ProgressChangedListener availableUpdateProgressChangedListener;

  private UpdateRepositoryManipulator repository;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  /**
   * Constructor.
   * @param repository The provider for update repository access that will be used
   */
  public UpdateProvider(UpdateRepositoryManipulator repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  /**
   * Registers a listener for the OnUpdateFetchingProgressChanged event. The event is fired when
   * the progress of fetching an {@link Update} changed after calling
   * {@link UpdateProvider#fetchUpdate(String, String)} or
   * {@link UpdateProvider#fetchUpdate(UpdateInfo, String)}.
   * @param listener the listener that will be invoked
   */
  public void setOnUpdateFetchingProgressChanged(ProgressChangedListener listener) {
    this.updateProgressChangedListener = listener;
  }

  /**
   * Registers a listener to the OnAvailableUpdateFetchingProgressChanged event.
   * The event is fired when the progress of fetching all available updates changed, i.e. after
   * calling {@link UpdateProvider#fetchAvailableUpdates(String)}.
   * @param listener the listener to invoke
   */
  public void setOnAvailableUpdateFetchingProgressChanged(ProgressChangedListener listener) {
    this.availableUpdateProgressChangedListener = listener;
  }

  /**
   * Fetches the {@link Update} that corresponds with the given {@link UpdateInfo}.
   * @param updateToFetch the update to fetch
   * @param channel the deployment channel to fetch the update from
   * @return the fetched {@link Update}
   */
  public CompletableFuture<Update> fetchUpdate(UpdateInfo updateToFetch, String channel) {
    return fetchUpdate(updateToFetch.getVersion(), channel);
  }

  /**
   * Fetches the {@link Update} with the given version.
   * @param version the version to fetch
   * @param channel the deployment channel to fetch the update from
   * @return the fetched {@link Update}
   */
  public CompletableFuture<Update> fetchUpdate(String version, String channel) {
    CompletableFuture<Update> future = new CompletableFuture<>();

    executorService.submit(() -> {
      try {
        File updatePackage = repository.pullUpdate(version, channel, updateProgressChangedListener);
        future.complete(Update.unpack(updatePackage));
      } catch (UpdateRepositoryException | UpdateException | IOException e) {
        future.completeExceptionally(e);
      } finally {
        ProgressChangedListener.fireComplete(updateProgressChangedListener);
      }
    });

    return future;
  }

  /**
   * Fetch all available updates.
   * @param channel the deployment channel to fetch the updates from
   * @return a list of {@link UpdateInfo} of all available updates in the specified channel
   */
  public CompletableFuture<List<UpdateInfo>> fetchAvailableUpdates(String channel) {
    CompletableFuture<List<UpdateInfo>> future = new CompletableFuture<>();

    executorService.submit(() -> {
      List<UpdateInfo> updateInfos = new ArrayList<>();
      try {
        List<File> updateInfoFiles = repository.updateInfoFiles(channel, availableUpdateProgressChangedListener);
        for (File f: updateInfoFiles) {
          updateInfos.add(UpdateInfo.readFromFile(f));
        }

        future.complete(updateInfos);
      } catch (UpdateRepositoryException | IOException e) {
        future.completeExceptionally(e);
      } finally {
        ProgressChangedListener.fireComplete(availableUpdateProgressChangedListener);
      }
    });

    return future;
  }
}
