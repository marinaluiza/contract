Feature: BDD for smart contract

  Background:
    Given I have created the smart contract
    And I have deployed the smart contract
    And I have added the locatario X
    And I have added the locador Y
    And I have added asset property

  Scenario: #1 Activate Contract
    Given I added the due date "2012-10-01"
    When now() is equal to due date
    Then the contract status is "InEffect"

  Scenario: #2 Succesfull Termination
    Given I added the due date "2012-10-01"
    And The asset is available
    And The asset has water
    And locatario paid rent
    When now() is equal to due date plus 12 months
    Then the contract status is "Succesfull Termination"

  Scenario: #3 Unsuccesfull Termination
    Given I added the due date "2012-10-01"
    And The asset is available
    And The asset has water
    And locatario did not paid rent
    Then the contract status is "Unsuccesfull Termination"


  # Você definiu um teste para verificar que o teste ainda não havia sido ativado? (Sim ou Não) - Não
  # Você definiu um teste para verificar se as 12 prestações de aluguel foram pagas no final de 1 ano e o contrato é terminado com sucesso? (Sim ou Não) - Não explicitamente (linha 19)
  # Você definiu um teste onde o locador não pagou a despesa da água e o contrato é cancelado e é cobrada a multa compensatória? (Sim ou Não) - Não
  # Você definiu um teste onde o locador não pagou os impostos e taxas e o contrato é cancelado e é cobrada a multa compensatória? (Sim ou Não) - Não
  # Você definiu um teste onde o locatário não pagou o aluguel e o contrato é cancelado e é cobrada a multa compensatória? (Sim ou Não) - Sim mas sem verificar a multa