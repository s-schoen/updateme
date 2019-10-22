package com.gmail.steffen1995.updateme.providers;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
  public void setup() throws IOException {
    // delete old files
    for (File f: tmp.toFile().listFiles()) {
      f.delete();
    }

    // copy structure from resources
    Files.copy(Paths.get(LocalUpdateRepositoryTest.class.getResource("/localTestRepo").getPath()), tmp, StandardCopyOption.REPLACE_EXISTING);
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

    assertEquals(2, channels.size());
    assertTrue(channels.contains("stable"));
    assertTrue(channels.contains("test"));
  }
}