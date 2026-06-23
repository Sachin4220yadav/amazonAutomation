package stepDefinations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import base.Base;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.B_Filters;

public class B_FiltersPrice extends Base {
	private static final Logger log = LogManager.getLogger(B_FiltersPrice.class);
	B_Filters filtersPage;

	@Given("User is viewing all products")
	public void user_is_viewing_all_products() throws Exception {
		log.info("User is viewing products right now");
		loadProperties();
		String currentUrl = getDriver().getCurrentUrl();
		System.out.println(currentUrl);
		// If we are not on a search results page, run a default search
		if (!currentUrl.contains("/s?") && !currentUrl.contains("k=")) {
			String product = prop.getProperty("product", "laptop");
			getDriver().get(getUrl() + "/s?k=" + product);
			Thread.sleep(2000);
		}
		Thread.sleep(2000);
	}

	@When("user apply rating filter")
	public void user_apply_rating_filter() throws InterruptedException {
		log.info("User is applying the rating filter");
		filtersPage = new B_Filters(getDriver());
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		((JavascriptExecutor) getDriver()).executeScript("window.scrollBy(0, 400)");
		Thread.sleep(2000);
		WebElement rating = wait.until(
			ExpectedConditions.elementToBeClickable(filtersPage.getRating()));
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", rating);
		Thread.sleep(3000);
	}

	@When("user also select a brand")
	public void user_also_select_a_brand() throws Exception {
		String brandName = prop.getProperty("brand", "HP");
		log.info("User selected the brand filter: " + brandName);
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		// Keep a handle on a current result so we can detect the page reload
		WebElement oldCard = getDriver().findElements(filtersPage.getProductCard()).get(0);

		WebElement brand = wait.until(
			ExpectedConditions.elementToBeClickable(filtersPage.brand(brandName)));
		((JavascriptExecutor) getDriver())
			.executeScript("arguments[0].scrollIntoView({block:'center'});", brand);
		Thread.sleep(1000);
		((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", brand);

		// The refinement reloads the result list - wait for the old card to go stale
		try {
			wait.until(ExpectedConditions.stalenessOf(oldCard));
		} catch (Exception e) {
			log.warn("Result list did not refresh after brand click");
		}
		wait.until(ExpectedConditions.presenceOfElementLocated(filtersPage.getProductCard()));
		System.out.println("URL after brand filter: " + getDriver().getCurrentUrl());
		Thread.sleep(1000);
	}

	@Then("user sees all the available products under these filters")
	public void user_sees_all_the_available_products_under_these_filters() throws InterruptedException {
		Thread.sleep(2000);
		List<WebElement> products = getDriver().findElements(filtersPage.getProductCard());
		System.out.println("Products after filters: " + products.size());
		Assert.assertTrue(products.size() > 0, "No products found after filters!");
		Thread.sleep(1000);
	}

	@When("user sort by price")
	public void user_sort_by_price() throws InterruptedException {
		log.info("Sorting by price: low to high");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, 0)");
		Thread.sleep(1000);
		WebElement sortDropdown = wait.until(
			ExpectedConditions.presenceOfElementLocated(filtersPage.getSortSelect()));
		Select select = new Select(sortDropdown);
		// Amazon's value for "Price: Low to High"
		select.selectByValue("price-asc-rank");
		Thread.sleep(3000);
	}

	@Then("user gets products in sorted price")
	public void user_gets_products_in_sorted_price() {
		// Read the price from inside each organic result card only.
		// Scraping the whole page would also pick up sponsored carousels and
		// "related products" strips, which are not part of the sorted column.
		String brandName = prop.getProperty("brand", "HP");
		List<WebElement> cards = getDriver().findElements(filtersPage.getProductCard());
		List<Integer> prices = new ArrayList<>();
		int organicCount = 0;
		int brandMatches = 0;
		for (WebElement card : cards) {
			// Skip sponsored cards - they are injected out of price order
			if (!card.findElements(org.openqa.selenium.By.xpath(
					".//*[contains(text(),'Sponsored')]")).isEmpty()) {
				continue;
			}
			List<WebElement> priceInCard = card.findElements(filtersPage.getPriceWhole());
			if (priceInCard.isEmpty()) {
				continue;
			}
			String raw = priceInCard.get(0).getText().replaceAll("[^0-9]", "");
			if (!raw.isEmpty()) {
				prices.add(Integer.parseInt(raw));
			}

			// Verify the brand filter actually took effect
			List<WebElement> titleEl = card.findElements(filtersPage.getTitle());
			if (!titleEl.isEmpty()) {
				organicCount++;
				String title = titleEl.get(0).getText();
				if (title.toLowerCase().contains(brandName.toLowerCase())) {
					brandMatches++;
				} else {
					System.out.println("  [non-" + brandName + " result] " + title);
				}
			}
		}
		System.out.println("Organic prices captured: " + prices);
		System.out.println("Brand match: " + brandMatches + "/" + organicCount
			+ " organic titles contain '" + brandName + "'");

		// Brand filter must have applied: every organic result is that brand
		Assert.assertTrue(organicCount > 0, "No organic results found!");
		Assert.assertEquals(brandMatches, organicCount,
			"Brand filter not applied - some results are not '" + brandName + "'");

		Assert.assertTrue(prices.size() > 0, "No prices visible after sorting!");
		// Verify the organic results are in non-decreasing order
		for (int i = 0; i < prices.size() - 1; i++) {
			Assert.assertTrue(prices.get(i) <= prices.get(i + 1),
				"Prices are not sorted ascending at index " + i
					+ " (" + prices.get(i) + " > " + prices.get(i + 1) + ")");
		}
	}
}
