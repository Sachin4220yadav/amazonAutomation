package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import base.Base;

public class B_Filters extends Base {

	public WebDriver driver;

	public B_Filters(WebDriver driver) {
		this.driver = driver;
	}

	// "4 Stars & Up" customer-review refinement
	By Rating = By.xpath("//*[contains(@aria-label,'4 Stars & Up')]");
	// Sort drop-down (native <select> element)
	By SortSelect = By.id("s-result-sort-select");
	// Each product card on the results page
	By ProductCard = By.cssSelector("div[data-component-type='s-search-result']");
	// Price (whole rupee part) inside a product card
	By PriceWhole = By.cssSelector("span.a-price-whole");
	// Product title inside a card (used to verify the brand filter)
	By Title = By.cssSelector("h2 span");

	// Brand refinement link, scoped to the Brands panel so it can never match a
	// product title. The brand name comes from data.properties.
	public By brand(String name) {
		return By.xpath("//*[@id='brandsRefinements']//a[.//span[normalize-space()='"
			+ name + "']] | //*[@id='filters']//a[.//span[normalize-space()='"
			+ name + "']]");
	}

	public WebDriver getDriver() {
		return driver;
	}

	public By getRating() {
		return Rating;
	}

	public void setRating(By rating) {
		Rating = rating;
	}

	public By getTitle() {
		return Title;
	}

	public By getSortSelect() {
		return SortSelect;
	}

	public void setSortSelect(By sortSelect) {
		SortSelect = sortSelect;
	}

	public By getProductCard() {
		return ProductCard;
	}

	public By getPriceWhole() {
		return PriceWhole;
	}
}
