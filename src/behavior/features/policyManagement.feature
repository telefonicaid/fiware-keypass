Feature: Create a new policy
  In order to express a new permission
  As a policy administrator
  We'll add a XACML Policy to the Access Control

  Scenario: Create new policy
    When I send a policy creation request to the Access Control for tenant "511" and subject "833"
    Then the Access Control returns a 201 OK and a payload with the ID
    And I can retrieve the created policy from the Access Control

  Scenario Outline: Request validation accepted
    Given I send a policy creation request to the Access Control for tenant "<tenant>" and subject "<subject>"
    When I send a validation request for tenant "<tenant>" with subject "<subject>", FRN "<frn>" and action "<action>"
    Then the Access Control should "<decision>" the access

  Examples:
    | tenant | subject | frn                               | action     | decision      |
    | 511    | 833     | frn:contextbroker:511:833:Device1 | read       | Permit        |
    | 511    | 833     | frn:contextbroker:511:833:Device1 | write      | Deny          |
    | 511    | 956     | frn:contextbroker:511:833:Device1 | read       | NotApplicable |

