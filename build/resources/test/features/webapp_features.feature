Feature: Process payments?
  Check the payment processor front end

  Scenario: User tries to process valid payment
    Given I am a user trying to process a payment
    When I submit correct details
    Then I should be told that the payment was successful

  Scenario Outline: User leaves out required field
    Given I am a user trying to process a payment
    When I submit a form with all data except "<fieldName>"
    Then I should be told that "<fieldName>" is required

    Examples:
      | fieldName |
      | customerName |
      | customerAddress |
      | cardNumber |
      | cardExpiryDate |
      | cardCvv |

  Scenario Outline: User enters invalid card number
    Given I am a user trying to process a payment
    When I submit a form with all data but with an "<invalidCard>"
    Then I should be told that the card is invalid
    Examples:
      | invalidCard |
      | 567709 |
      | 6767o962 |

  Scenario Outline: User enters a card number with invalid prefixes
    Given I am a user trying to process a payment
    When I submit a form with all data with "<cardNumber>" and "<cardType>"
    Then I should be told that the card prefix does not match
    Examples:
      | cardType | cardNumber |
      | AMERICAN_EXPRESS | 4557 3727 3979 3901 |
      | AMERICAN_EXPRESS | 5124 0021 8814 1135 |
      | VISA | 371449635398431 |
      | VISA | 5124 0021 8814 1135 |
      | MASTER_CARD | 371449635398431 |

    Scenario Outline: User enters an invalid date
      Given I am a user trying to process a payment
      When I submit a form with all data but "<date>" is invalid
      Then I should be told date is invalid
      Examples:
        | date |
        | 10/17 |
        | 14/2o20 |

  Scenario Outline: User enters valid card details with different card types
    Given I am a user trying to process a payment
    When I submit a form with all data with "<cardNumber>" and "<cardType>"
    Then I should be told that the payment was successful
    Examples:
      | cardType | cardNumber |
      | AMERICAN_EXPRESS | 371449635398431 |
      | VISA | 4557 3727 3979 3901 |
      | MASTER_CARD | 5124 0021 8814 1135 |

  Scenario: User enters data and resets it
    Given I am a user trying to process a payment
    When I submit correct details
    And click on the clear button
    Then the form data should be cleared