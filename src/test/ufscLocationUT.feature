Feature: UFSCLocation Creation
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the tenant UFSC
    And I have added the landlord FarmOwner
    And I have added asset farm

  Scenario: UFSCLocation successful termination #1
    Given is inEffect
    And obligations 2.C is not fullfilled
    When obligations 2.B is fullfilled
    And penaulty 9.U is apllied
    Then the contract status is "Unsuccessful Termination"

  Scenario: UFSCLocation successful termination #2
    Given is inEffect
    And obligations 3.B is not fullfilled
    When obligations 2.B is fullfilled
    And penaulty 9.U is apllied
    Then the contract status is "Unsuccessful Termination"

  Scenario: UFSCLocation successful termination #3
    Given is inEffect
    And obligations 3.G is not fullfilled
    When obligations 2.B is fullfilled
    And penaulty 9.U is apllied
    Then the contract status is "Unsuccessful Termination"


