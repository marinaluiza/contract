Feature: UFSCBuyPaper Creation
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the seller with name 'dicapel'
    And I have added the buyer with name 'ufsc'
    And I have added the buyer the address 'Campus Reitor...'

  Scenario: Create a UFSCBuyPaper - happy path
    Given contract creation data is "2019-07-24"
    When now() is after than creation data
    And obligations 2.2 is created
    And obligations 5.1 is created
    And obligations 7.5 is created
    Then the contract status is "Created"

  Scenario: Activate a UFSCBuyPaper
    Given contract creation data is "2019-07-24"
    When now() is after than creation data
    Then obligations 2.2 is activated
    And obligations 5.1 is activated
    And obligations 7.5 is activated
    And the contract status is "Activated"

Feature: UFSCBuyPaper Successful Termination
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the seller with name 'dicapel'
    And I have added the buyer with name 'ufsc'
    And I have added the buyer the address 'Campus Reitor...'


  Scenario: UFSCLocation successful termination #1
    Given contract is in inEffect
    When obligations 2.2 is fullfilled
    And obligations 5.1 is fullfilled
    And obligations 7.5 is fullfilled
    Then the contract status is "Successful Termination"

  Scenario: UFSCLocation successful termination #2
    Given contract is in inEffect
    When obligations 5.6 is fullfilled
    And obligations 5.1 is fullfilled
    And obligations 7.5 is fullfilled
    Then the contract status is "Successful Termination"

  Scenario: UFSCLocation successful termination #3
    Given contract is in inEffect
    When obligations 2.2 is fullfilled
    And obligations 5.1 is fullfilled
    And obligations 7.6 is fullfilled
    Then the contract status is "Successful Termination"

Feature: UFSCLocation Unsuccessful Termination
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the seller with name 'dicapel'
    And I have added the buyer with name 'ufsc'
    And I have added the buyer the address 'Campus Reitor...'

  Scenario: UFSCLocation unsuccessful termination #1
    Given contract is inEffect
    When power 11.2.2 is exerted
    Then the contract status is "Unsuccessful Termination"

  Scenario: UFSCLocation unsuccessful termination #2
    Given contract is inEffect
    And obligations 2.2 is fullfilled
    And obligations 5.1 is fullfilled
    When obligation 7.5 expires
    Then the contract status is "Unsuccessful Termination"




