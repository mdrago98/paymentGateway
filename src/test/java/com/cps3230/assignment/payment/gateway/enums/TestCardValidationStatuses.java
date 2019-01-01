package com.cps3230.assignment.payment.gateway.enums;

public enum TestCardValidationStatuses {
  VALID,
  LUHN_FAILURE,
  CARD_BRAND_NOT_VALID,
  PREFIX_NOT_VALID,
  DATE_PARSE_FAILURE,
  CARD_EXPIRED,
  EMPTY_FIELDS
}
