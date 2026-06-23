package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import base.Base;

public class A_Home extends Base {
	public WebDriver driver;

	public A_Home(WebDriver driver) {
		this.driver = driver;
	}

	// Amazon home page search elements
	By SearchBox = By.id("twotabsearchtextbox");
	By SearchButton = By.id("nav-search-submit-button");
	// Each product card on the results page
	By ProductCard = By.cssSelector("div[data-component-type='s-search-result']");

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public By getSearchBox() {
		return SearchBox;
	}

	public void setSearchBox(By searchBox) {
		SearchBox = searchBox;
	}

	public By getSearchButton() {
		return SearchButton;
	}

	public void setSearchButton(By searchButton) {
		SearchButton = searchButton;
	}

	public By getProductCard() {
		return ProductCard;
	}

	public void setProductCard(By productCard) {
		ProductCard = productCard;
	}
}
