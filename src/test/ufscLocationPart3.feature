Feature: UFSCLocation Creation
  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the tenant UFSC
    And I have added the landlord FarmOwner
    And I have added asset farm

  Scenario: Create a UFSCLocation - happy path
    Given contract creation data is "2012-09-26"
    When now() is after than creation data
    And obligations 2.B is created
    And obligations 2.C is created
    And obligations 5.U is created
    And obligations 3.A is created
    And obligations 3.B is created
    And obligations 3.G is created
    Then the contract status is "Created"

  Scenario: Activate a UFSCLocation
    Given contract effect date is "2012-10-2012"
    When now() is after than creation data
    And obligations 3.A is Activated
    Then obligations 3.B is Activated
    And obligations 3.G is Activated
    And obligations 2.C is Activated
    And the contract status is "Activated"