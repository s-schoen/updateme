package com.gmail.steffen1995.updateme;

import com.gmail.steffen1995.updateme.update.Update;
import com.gmail.steffen1995.updateme.update.UpdateException;
import com.gmail.steffen1995.updateme.update.UpdateInfo;
import com.gmail.steffen1995.updateme.update.UpdateObject;
import com.gmail.steffen1995.updateme.util.HashCalculator;
import com.gmail.steffen1995.updateme.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for classes regarding updates.
 * @author Steffen Schoen
 */
public class UpdateTests {
  private static final String OBJ1_SHA256 = "9be104294df7d5a59c328241d49ac062e2c7b9660636e7f511e3a1dc3d919377";
  private static final String OBJ2_SHA256 = "f5ccc99ee743da880de3fa58ef5f0a6306691f5f15c340b5ba11fb58e32b2739";

  private Update update;
  private UpdateObject object1, object2;

  @Before
  public void setup() throws IOException {
    update = new Update("1.0.0");

    object1 = new UpdateObject(UpdateTests.class.getResource("/testFiles/textFile.txt").getPath(), "/");
    object2 = new UpdateObject(UpdateTests.class.getResource("/testFiles/binaryFile.bin").getPath(), "/");

    update.getUpdateObjects().add(object1);
    update.getUpdateObjects().add(object2);

    object1.calculateChecksum();
    object2.calculateChecksum();
  }

  @Test
  public void filesFromDirectory() throws IOException {
    List<UpdateObject> updateObjects = UpdateObject.fromDirectory(new File(UpdateTests.class.getResource("/testFiles").getPath()), "/");

    assertEquals(2, updateObjects.size());
  }

  @Test
  public void checksumCalculation() {
    assertEquals(OBJ1_SHA256, object1.getChecksum());
    assertEquals(OBJ2_SHA256, object2.getChecksum());
  }

  @Test
  public void testInfoCreationFromUpdate() {
    UpdateInfo info = UpdateInfo.fromUpdate(update);

    assertEquals(info.getVersion(), update.getVersion());
    assertEquals(update.getPublishDate(), info.getPublishDate());

    UpdateInfo.FileUpdate fileUpdate1 = info.getFileUpdates().get(0);
    assertEquals(20, fileUpdate1.getSizeBytes());
    assertEquals(OBJ1_SHA256, fileUpdate1.getChecksum());
    assertEquals("/textFile.txt", fileUpdate1.getLocalPath());

    UpdateInfo.FileUpdate fileUpdate2 = info.getFileUpdates().get(1);
    assertEquals(116, fileUpdate2.getSizeBytes());
    assertEquals( OBJ2_SHA256, fileUpdate2.getChecksum());
    assertEquals("/binaryFile.bin", fileUpdate2.getLocalPath());
  }

  @Test
  public void updateInfoSerialization() throws IOException {
    UpdateInfo info = UpdateInfo.fromUpdate(update);

    File infoFile = File.createTempFile("updateme", "junit");
    infoFile.deleteOnExit();

    info.writeToFile(infoFile.getAbsolutePath());

    UpdateInfo parsed = UpdateInfo.readFromFile(infoFile);

    assertEquals(info.getVersion(), parsed.getVersion());
    TestUtils.assertEqualDates(info.getPublishDate(), parsed.getPublishDate());
    assertEquals(2, parsed.getFileUpdates().size());
  }

  @Test
  public void packTest() throws IOException, UpdateException {
    Path tmpDir = Files.createTempDirectory("updateme");
    String updateFile = Paths.get(tmpDir.toString(), "update.zip").toString();

    update.pack(updateFile);
    Update unpacked = Update.unpack(updateFile);

    assertEquals(update.getVersion(), unpacked.getVersion());
    TestUtils.assertEqualDates(update.getPublishDate(), unpacked.getPublishDate());
    assertEquals(update.getUpdateObjects().size(), unpacked.getUpdateObjects().size());

    for (UpdateObject uo: update.getUpdateObjects()) {
      for (UpdateObject uo2: unpacked.getUpdateObjects()) {
        if (uo2.getChecksum().equals(uo.getChecksum())){
          Path unpackedFile = Paths.get(uo2.getFile().getAbsolutePath());

          assertTrue(Files.exists(unpackedFile));
          String hash = HashCalculator.sha256(new File(unpackedFile.toString()));
          assertEquals(uo.getChecksum(), hash);
        }
      }
    }
  }

  @Test(expected = UpdateException.class)
  public void unpackInfoMissing() throws IOException, UpdateException {
    Update.unpack(UpdateTests.class.getResource("/update_info_missing.zip").getPath());
  }

  @Test(expected = UpdateException.class)
  public void unpackFileMissing() throws IOException, UpdateException {
    Update.unpack(UpdateTests.class.getResource("/update_file_missing.zip").getPath());
  }

  @Test(expected = UpdateException.class)
  public void unpackChecksumWrong() throws IOException, UpdateException {
    Update.unpack(UpdateTests.class.getResource("/update_checksum_wrong.zip").getPath());
  }

  @Test(expected = IOException.class)
  public void updateInfoWrongDateFormat() throws IOException {
    UpdateInfo.readFromFile(UpdateTests.class.getResource("/updateInfo_wrong_data_format.json").getPath());
  }
}
