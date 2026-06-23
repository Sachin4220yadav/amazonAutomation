@search
Feature: Search functionality on Amazon

Scenario: confirmation for home page
Given User is on home page
When user searches for a product
Then user sees all the available products
