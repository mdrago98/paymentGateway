package com.cps3230.assignment.payment.gateway.utils;

public class StringUtils {

  /**
   * A helper function that checks if a string is null or empty.
   *
   * @param string The string to be checked
   * @return true IFF string is null or empty
   */
  public static boolean isNullOrEmpty(String string) {
    return string == null || string.trim().isEmpty();
  }
}
