Feature: UFSCLocation Creation
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the tenant UFSC
    And I have added the landlord FarmOwner
    And I have added asset farm

  Scenario: UFSCLocation successful termination #1
    Given is inEffect
    And obligations 5.U is fullfilled
    When obligations 2.B is fullfilled
    And the contract status is "Successful Termination"

