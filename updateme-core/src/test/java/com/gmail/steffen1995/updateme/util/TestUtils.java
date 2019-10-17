package com.gmail.steffen1995.updateme.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Steffen Schoen
 */
public class TestUtils {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

  public static void assertEqualDates(Date expected, Date actual){
    assertEquals(DATE_FORMAT.format(expected), DATE_FORMAT.format(actual));
  }
}
