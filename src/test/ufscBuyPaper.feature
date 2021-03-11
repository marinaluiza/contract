Feature: BDD for smart contract

  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the seller with name 'dicapel'
    And I have added the buyer with name 'ufsc'
    And I have added the buyer the address 'Campus Reitor...'

  Scenario: #1 Activate Contract
    Given I added the effective date "2019-02-01"
    When now() is equal to due date
    Then the contract status is "InEffect"

  Scenario: #2 Succesfull Termination
    Given I added the effective date "2019-02-01"
    And The buyer made a solicitation on the day "2019-03-11"
    And The seller derivered the asset on the day "2019-03-15"
    When The buyer pay on the day "2019-04-01"
    Then the contract status is "Succesfull Termination"

  Scenario: #3 Unsuccesfull Termination
    Given I added the effective date "2019-02-01"
    And The buyer made a solicitation on the day "2019-03-11"
    When The seller derivered the asset on the day "2019-03-30"
    Then the contract status is "Unsuccesfull Termination"

  Scenario: #4 Unsuccesfull Termination
    Given I added the effective date "2019-02-01"
    And The buyer made a solicitation on the day "2019-03-11"
    And The seller derivered the asset on the day "2019-03-15"
    When The do not paid the delivery asset
    Then the contract status is "Unsuccesfull Termination"

