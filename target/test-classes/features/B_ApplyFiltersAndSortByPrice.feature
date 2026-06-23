@filterByPrice
Feature: Filters applied

Scenario: confirmatio
Given User is viewing all products
When user apply rating filter
And user also select a brand
Then user sees all the available products under these filters
When user sort by price
Then user gets products in sorted price
