package com.gmail.steffen1995.updateme.providers;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateException;
import com.gmail.steffen1995.updateme.update.UpdateInfo;
import com.gmail.steffen1995.updateme.util.TestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * test for com.gmail.steffen1995.updateme.providers.LocalUpdateRepository class
 *
 * @author Steffen Schoen
 **/
public class LocalUpdateRepositoryTest {
  private static Path tmp;
  private static LocalUpdateRepository repo;

  @BeforeClass
  public static void setupOnce() throws IOException {
    tmp = Files.createTempDirectory("updateme");
    repo = new LocalUpdateRepository(tmp.toFile().getAbsolutePath());
  }

  @Before
  public void setupRepo() throws IOException {
    // copy structure from resources
    FileUtils.copyDirectory(new File(LocalUpdateRepositoryTest.class.getResource("/localTestRepo").getPath()), tmp.toFile());
  }

  @After
  public void clearDirectory() throws IOException {
    // delete old files
    FileUtils.cleanDirectory(tmp.toFile());
  }

  @Test
  public void init() throws UpdateRepositoryException, IOException {
    File tmpBase = Files.createTempDirectory("updateme").toFile();
    LocalUpdateRepository tmpRepo = new LocalUpdateRepository(tmpBase);

    tmpRepo.initStructure();

    assertTrue(Files.exists(Paths.get(tmpBase.toString(), UpdateRepositoryManipulator.DEFAULT_CHANNEL)));
    assertTrue(Files.isDirectory(Paths.get(tmpBase.toString(), UpdateRepositoryManipulator.DEFAULT_CHANNEL)));
  }

  @Test
  public void createChannel() throws UpdateRepositoryException {
    String channelName = "testChannel";

    repo.createChannel(channelName);

    assertTrue(Files.exists(Paths.get(tmp.toString(), channelName)));
    assertTrue(Files.isDirectory(Paths.get(tmp.toString(), channelName)));
  }

  @Test(expected = UpdateRepositoryException.class)
  public void createChannelAlreadyExists() throws UpdateRepositoryException {
    repo.createChannel("stable");
  }

  @Test
  public void availableChannels() throws UpdateRepositoryException {
    List<String> channels = repo.availableChannels();

    System.out.println(Arrays.toString(channels.toArray()));

    assertEquals(2, channels.size());
    assertTrue(channels.contains("stable"));
    assertTrue(channels.contains("test"));
  }

  @Test
  public void pushUpdate() throws UpdateRepositoryException, IOException, UpdateException {
    File update = new File(LocalUpdateRepositoryTest.class.getResource("/valid_update.zip").getPath());
    Update u = Update.unpack(update);

    repo.pushUpdate(update, "test");

    assertTrue(Files.exists(Paths.get(tmp.toString(), "test", "1.0.0", "data.zip")));
    assertTrue(Files.exists(Paths.get(tmp.toString(), "test", "1.0.0", "updateInfo.json")));

    UpdateInfo info = UpdateInfo.readFromFile(Paths.get(tmp.toString(), "test", "1.0.0", "updateInfo.json").toString());

    assertEquals(u.getVersion(), info.getVersion());
    assertEquals(u.getUpdateObjects().size(), info.getFileUpdates().size());
    TestUtils.assertEqualDates(u.getPublishDate(), info.getPublishDate());
    TestUtils.assertEqualDates(u.getPublishDate(), info.getPublishDate());
    TestUtils.assertEqualDates(u.getPublishDate(), info.getPublishDate());
  }

  @Test
  public void pullUpdate() throws UpdateRepositoryException {
    File updatePackage = repo.pullUpdate("1.0.0", "stable");

    assertEquals(Paths.get(tmp.toString(), "stable", "1.0.0", "data.zip").toString(), updatePackage.getAbsolutePath());
  }

  @Test(expected = UpdateRepositoryException.class)
  public void pullUpdateNoVersion() throws UpdateRepositoryException {
    repo.pullUpdate("2.0.0", "stable");
  }

  @Test(expected = UpdateRepositoryException.class)
  public void pullUpdateNoPackage() throws UpdateRepositoryException {
    repo.pullUpdate("2.0.0", "test");
  }

  @Test
  public void updateInfoFiles() throws UpdateRepositoryException {
    List<File> updateInfos = repo.updateInfoFiles("stable");

    assertEquals(1, updateInfos.size());
    assertEquals(Paths.get(tmp.toString(), "stable", "1.0.0", "updateInfo.json").toString(), updateInfos.get(0).getAbsolutePath());
  }
}