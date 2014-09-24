Feature: Create a new policy
  In order to express a new permission
  As a policy administrator
  We'll add a XACML Policy to the Access Control

  Scenario: Create new policy
    When I send a policy creation request to the Access Control for tenant "511" and subject "833"
    Then the Access Control returns a "201" code and a payload with the ID
    And I can retrieve the created policy from the Access Control

  Scenario Outline: Request validation
    Given I send a policy creation request to the Access Control for tenant "<tenant>" and subject "<subject>"
    When I send a validation request for tenant "<tenant>" with subject "<subject>", FRN "<frn>" and action "<action>"
    Then the Access Control should "<decision>" the access

  Examples:
    | tenant | subject | frn                               | action     | decision      |
    | 511    | 833     | frn:contextbroker:511:833:Device1 | read       | Permit        |
    | 511    | 833     | frn:contextbroker:511:833:Device1 | write      | Deny          |
    | 511    | 956     | frn:contextbroker:511:833:Device1 | read       | NotApplicable |

  Scenario: Remove a policy
    Given I send a policy creation request to the Access Control for tenant "615" and subject "818"
    When I send a remove request for the last request
    And I get the list of policies for the tenant "615" and subject "818"
    Then trying to get the policy raises a 404

  Scenario: Remove a tenant
    Given I send a policy creation request to the Access Control for tenant "634" and subject "467"
    When I send a remove request for tenant "634"
    Then trying to get the policy raises a 404

  Scenario: Remove a subject
    Given I send a policy creation request to the Access Control for tenant "634" and subject "467"
    When I send a remove request for subject "467" in tenant "634"
    Then trying to get the policy raises a 404

  Scenario: List policies
    Given I send the following policies to the access control:
      | tenant | subject | id         |
      | 515    | 833     | 11111111   |
      | 515    | 967     | 22222222   |
      | 515    | 833     | 33333333   |
    When I get the list of policies for the tenant "515" and subject "833"
    Then the number of policies in the list is "2"
    
  Scenario: Policy modification
    Given I send a policy creation request to the Access Control for tenant "511" and subject "833"
    When I modify the policy
    Then the Access Control returns a "200" code and a payload with the ID

  Scenario: Two subjects in request
    Given I send a policy creation request to the Access Control for tenant "511" and subject "833"
    And I send a validation request for tenant "511" with subjects "833" and "433", FRN "frn:contextbroker:511:833:Device1" and action "read"
    Then the Access Control should "Permit" the access

  Scenario: Policies are cached, thus changing it does not take effect instantly
    Given I send a policy creation request to the Access Control for tenant "511" and subject "833"
    When I modify the policy
    And I send a validation request for tenant "511" with subject "833", FRN "frn:contextbroker:511:833:Device1" and action "read"
    Then the Access Control should "Permit" the access

  Scenario: Policies modification with cache miss
    Given I send a policy creation request to the Access Control for tenant "511" and subject "833"
    When I modify the policy
    And I send a validation request for tenant "511" with subjects "833" and "844", FRN "frn:contextbroker:511:833:Device1" and action "read"
    Then the Access Control should "Deny" the access

  Scenario: Invalid policies are rejected
    Given I send a invalid policy for tenant "511" and subject "833"
    Then the Access Control returns a "400"
