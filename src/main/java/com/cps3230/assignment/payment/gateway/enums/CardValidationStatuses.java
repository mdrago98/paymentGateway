package com.cps3230.assignment.payment.gateway.enums;

/**
 * An enumeration representing validation statuses.
 */
public enum CardValidationStatuses {
  VALID,
  LUHN_FAILURE,
  CARD_BRAND_NOT_VALID,
  PREFIX_NOT_VALID,
  DATE_PARSE_FAILURE,
  CARD_EXPIRED,
  EMPTY_FIELDS
}
