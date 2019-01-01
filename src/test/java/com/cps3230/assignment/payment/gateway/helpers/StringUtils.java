package com.cps3230.assignment.payment.gateway.helpers;

public class StringUtils {
  public static boolean isStringUpperCase(String string) {
    boolean isCardUpperCase = true;
    for(char character: string.toCharArray()){
      if (Character.isLetter(character) && !Character.isUpperCase(character)) {
        isCardUpperCase = false;
      }
    }
    return isCardUpperCase;
  }
}
