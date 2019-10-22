package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateException;
import com.gmail.steffen1995.updateme.update.UpdateInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link UpdateProvider} the fetches updates from the local file system,
 * i.e. from a base directory that contains all updates.
 * @author Steffen Schoen
 */
public class LocalUpdateProvider extends UpdateProvider {
  private File baseDirectory;
  ExecutorService executorService = Executors.newSingleThreadExecutor();

  public LocalUpdateProvider(File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public LocalUpdateProvider(String baseDirectory) {
    this(new File(baseDirectory));
  }

  @Override
  public Future<Update> fetchUpdate(String version) throws IOException, UpdateException {
    return null;
  }

  @Override
  public Future<List<UpdateInfo>> fetchAvailableUpdates(String channel) throws IOException, UpdateException {
    checkBaseDirectory();

    FutureTask<List<UpdateInfo>> listFutureTask = new FutureTask<List<UpdateInfo>>(() -> {
      return null;
    });

    executorService.execute(listFutureTask);
    return listFutureTask;
  }

  private void checkBaseDirectory() throws UpdateException {
    if (!baseDirectory.exists()) {
      throw new UpdateException("Base directory does not exist");
    }
    if (!baseDirectory.isDirectory()) {
      throw new UpdateException("The base directory has to be a directory.");
    }
  }
}
