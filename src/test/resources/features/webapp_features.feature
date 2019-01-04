Feature: Process payments?
  Check the payment processor front end

  Scenario: User tries to process valid payment
    Given I am a user trying to process a payment
    When I submit correct details
    Then I should be told that the payment was successful