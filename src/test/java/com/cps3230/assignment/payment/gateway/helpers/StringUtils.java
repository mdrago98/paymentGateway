package com.cps3230.assignment.payment.gateway.helpers;

public class StringUtils {

  /**
   * A helper function that checks if a string is upper case.
   * @param string the string to check
   * @return true IFF the string is upper case
   */
  public static boolean isStringUpperCase(String string) {
    boolean isCardUpperCase = true;
    for (char character : string.toCharArray()) {
      if (Character.isLetter(character) && !Character.isUpperCase(character)) {
        isCardUpperCase = false;
      }
    }
    return isCardUpperCase;
  }
}
