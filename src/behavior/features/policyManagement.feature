Feature: Create a new policy
  In order to express a new permission
  As a policy administrator
  We'll add a XACML Policy to the Access Control

  Scenario: Create new policy
    When I send a policy creation request to the Access Control
    Then the Access Control returns a 201 OK and a payload with the ID
    And I can retrieve the created policy from the Access Control

