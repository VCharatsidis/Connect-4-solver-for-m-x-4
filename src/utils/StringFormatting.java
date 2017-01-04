package utils;

import java.text.DecimalFormat;

public class StringFormatting {

  /** Formats a double value to a string with the given number of decimal digits */
  public static String decimalFormat(double value, int decimalDigits) {
    String format = "#0.";
    for (int i = 0; i < decimalDigits; i++)
      format += "#";
    DecimalFormat df = new DecimalFormat(format);
    // df.setMinimumFractionDigits(1);
    df.setMinimumIntegerDigits(1);
    return df.format(value);
  }

  /** Formats a double value to a string with the given number of decimal digits */
  public static String decimalFormat(double value, int decimalDigits, int minimumDecimalDigits) {
    String format = "#0.";
    for (int i = 0; i < decimalDigits; i++)
      format += "#";
    DecimalFormat df = new DecimalFormat(format);
    df.setMinimumFractionDigits(minimumDecimalDigits);
    df.setMinimumIntegerDigits(1);
    return df.format(value);
  }
}
