package com.cps3230.assignment.payment.gateway.constants;

import com.cps3230.assignment.payment.gateway.enums.CardBrands;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardLengths {

  /**
   * A helper function that returns Card Brands mapped with Card Lengths.
   *
   * @return a map representation of the card lengths.
   */
  public static Map<CardBrands, List<Integer>> getCardLengths() {
    return Map.ofEntries(Map.entry(CardBrands.AMERICAN_EXPRESS, Collections.singletonList(15)),
        Map.entry(CardBrands.MASTER_CARD, Collections.singletonList(16)),
        Map.entry(CardBrands.VISA, Arrays.asList(13, 16)));
  }
}
