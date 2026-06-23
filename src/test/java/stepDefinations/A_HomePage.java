package stepDefinations;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import base.Base;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pageObjects.A_Home;

public class A_HomePage extends Base {
	private static final Logger log = LogManager.getLogger(A_HomePage.class);
	A_Home homePage;

	@Given("User is on home page")
	public void user_is_on_home_page() throws Exception {
		log.info("Website initialised");
		getDriver().get(getUrl());
	}

	@When("user searches for a product")
	public void user_searches_for_a_product() throws Exception {
		log.info("User is searching for a product");
		homePage = new A_Home(getDriver());
		String product = prop.getProperty("product", "laptop");

		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		WebElement search = wait.until(
			ExpectedConditions.elementToBeClickable(homePage.getSearchBox()));
		search.clear();
		search.sendKeys(product);
		Thread.sleep(1000);
		// Enter triggers the search even if the button id changes
		search.sendKeys(Keys.ENTER);
		Thread.sleep(3000);
	}

	@Then("user sees all the available products")
	public void user_sees_all_the_available_products() throws Exception {
		log.info("All relevant products fetched");
		WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
		wait.until(ExpectedConditions.presenceOfElementLocated(homePage.getProductCard()));
		List<WebElement> products = getDriver().findElements(homePage.getProductCard());
		System.out.println("Products found: " + products.size());
		Assert.assertTrue(products.size() > 0, "No products found on search!");
	}
}
