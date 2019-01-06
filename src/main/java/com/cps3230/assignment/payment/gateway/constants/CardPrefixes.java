package com.cps3230.assignment.payment.gateway.constants;

import com.cps3230.assignment.payment.gateway.enums.CardBrands;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CardPrefixes {

  /**
   * A 2D array containing a prefix and an enum showing what brand of card that prefix belongs to.
   *
   * @return a collection of valid prefixes
   */
  public static Map<CardBrands, List<String>> validPrefixes() {
    return Map.ofEntries(Map.entry(CardBrands.AMERICAN_EXPRESS, Arrays.asList("34", "37")),
        Map.entry(CardBrands.MASTER_CARD, Arrays.asList("51", "52", "53", "54", "55")),
        Map.entry(CardBrands.VISA, Collections.singletonList("4")));
  }

}
