package org.mytests;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import org.myutilities.Utilities;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author puneeth
 * 
 * Regression Test Suite
 * 
 * tested on Mac platform on the Catalina OS with latest Chrome and Firefox browsers
 *
 */
/**
 * @author puneeth
 *
 */
public class Tests {
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Properties properties;
	private static InputStream input;

	private static final String URL = "url";
	private static final String HEADER = "Play sample application — Computer database";
	private static final String ADD_COMPUTER_PHRASE_KEY = "addComputerPhrase";
	private static final String COMPUTERS_FOUND_PHRASE_KEY = "computers found";
	private static final String NAME_KEY = "name";
	private static final String INTRODUCED_KEY = "introduced";
	private static final String DISCONTINUED_KEY = "discontinued";
	private static final String COMPANY_KEY = "company";

	/**
	 * Before class routine for setup
	 * 
	 * @param browser
	 * @throws IOException
	 */
	@BeforeClass
	@Parameters(value = { "browser" })
	public void beforeClass(final String browser) throws IOException {
		// Initialize browser drivers
		if (browser.equals("Chrome")) {
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/lib/chromedriver");
			driver = new ChromeDriver();
		} else if (browser.equals("Firefox")) {
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/lib/geckodriver");
			driver = new FirefoxDriver();
		}
		wait = new WebDriverWait(driver, 10l);

		// Load the properties file for the locale specific strings
		input = new FileInputStream(System.getProperty("user.dir") + "/src/org/mytests/" + "enUS.properties");
		properties = new Properties();
		properties.load(input);
	}

	/**
	 * After class routine for cleanup
	 */
	@AfterTest
	public void cleanUp() {
		driver.quit();
	}

	@Test
	public void checkIfComputersLinkTextIsPresent() {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Get the desired element: Header text
		driver.findElement(By.linkText(HEADER));
		WebElement firstResult = wait.until(presenceOfElementLocated(By.linkText(HEADER)));

		// Get the attribute and assert
		final String actual = firstResult.getAttribute("textContent");
		Assert.assertEquals(actual.trim(), HEADER, "The header is incorrect");
	}

	@Test
	public void checkIfTextIsPresent() {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Check for the computers found phrase
		Assert.assertTrue(driver.getPageSource().contains(properties.getProperty(COMPUTERS_FOUND_PHRASE_KEY)),
				"The number of computers phrase is not present");
	}

	@Test
	public void addComputerAndCheckByClickingNext() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Fill up the form with info
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-12");
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-12");
		Select dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		dropdown.selectByVisibleText("Thinking Machines");

		// Click the Submit button
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if the new computer is in the table
		Assert.assertTrue(checkIfSpecificRowIsInTable(name), "The newly added computer is not present in the table");
	}

	@Test
	public void addAndSearchComputer() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Fill up the form with info
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-12");
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-12");
		Select dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		dropdown.selectByVisibleText("Thinking Machines");

		// Click the Submit button
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Search for the newly added computer
		Assert.assertTrue(searchComputer(name), "Cannot find the newly added computer");

		// Click the newly added computer
		driver.findElement(By.linkText(name)).click();
		Thread.sleep(1000);

		// Check if the newly added computer has the correct data
		String actualName = driver.findElement(By.id(properties.getProperty(NAME_KEY))).getAttribute("value");
		String actualIntroduced = driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY)))
				.getAttribute("value");
		String actualDiscontinued = driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY)))
				.getAttribute("value");
		dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		WebElement option = dropdown.getFirstSelectedOption();
		String actualCompany = option.getText();

		Assert.assertTrue(actualName.equals(name), "The computer name does not seem to be updated");
		Assert.assertTrue(actualIntroduced.equals("9999-12-12"), "The introduced date does not seem to be updated");
		Assert.assertTrue(actualDiscontinued.equals("9999-12-12"),
				"The discontinued date name does not seem to be updated");
		Assert.assertTrue(actualCompany.equals("Thinking Machines"), "The company does not seem to be updated");
	}

	@Test
	public void checkIfTableHeadersAreCorrect() {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Check if the table headers are correct
		List<WebElement> list = driver.findElements(By.xpath("//table/thead/tr[1]/th"));
		Assert.assertTrue(list.size() == 4, "The count of the table headers is incorrect");
		Assert.assertEquals(list.get(0).getAttribute("textContent").trim(), "Computer name");
		Assert.assertEquals(list.get(1).getAttribute("textContent").trim(), "Introduced");
		Assert.assertEquals(list.get(2).getAttribute("textContent").trim(), "Discontinued");
		Assert.assertEquals(list.get(3).getAttribute("textContent").trim(), "Company");
	}

	@Test
	public void checkIfComputerCountMatchesTheNumberOfEntriesInTheTable() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Add the computer names from the first page
		List<String> names = new ArrayList<String>();
		List<WebElement> namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
		for (WebElement nameEle : namesElements) {
			names.add(nameEle.getText());
		}

		// locating next button
		WebElement nextElement = driver.findElement(By.partialLinkText("Next"));
		WebElement p = nextElement.findElement(By.xpath("./.."));
		String nextButtonClass = p.getAttribute("class");

		// Keep clicking Next till the last page
		while (!nextButtonClass.contains("disabled")) {
			driver.findElement(By.partialLinkText("Next")).click();
			Thread.sleep(1000);

			// Add the computer names to the list
			namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
			for (WebElement nameEle : namesElements) {
				names.add(nameEle.getText());
			}
			nextElement = driver.findElement(By.partialLinkText("Next"));
			p = nextElement.findElement(By.xpath("./.."));
			nextButtonClass = p.getAttribute("class");
		}

		// Check if the "computers found" phrase has the right number
		Assert.assertTrue(driver.getPageSource().contains(names.size() + " computers found"),
				"The number of computers phrase is not present");
	}

	@Test
	public void addComputerWithoutProvidingComputerName() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Just click the Submit button
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Assert if the Required phrase is present
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"), "The required phrase is not present");
	}

	@Test
	public void addComputerWithBlankComputerName() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide a "Blank" string for the computer name and click the Submit button
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys("");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Assert if the Required phrase is present
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"), "The required phrase is not present");
	}

	@Test
	public void addComputerWithNonASCIIComputerName() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide a Non-ASCII string for the computer name and click the Submit button
		String name = "भारत" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if the computer has been added
		Assert.assertTrue(searchComputer(name), "Cannot find the newly added computer");
	}

	@Test
	public void addComputerWithInvalidDateFormatForIntroduced() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid date format for the introduced field and click the Submit
		// button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("12-12-9999");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateMonthForIntroduced() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid month for the introduced field and click the Submit button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-13-31");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateDayForIntroduced() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid day for the introduced field and click the Submit button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-32");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateYearForIntroduced() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid year for the introduced field and click the Submit button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("-12-32");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateFormatForDiscontinued() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid date format for the discontinued field and click the
		// Submit button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("12-12-9999");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateMonthForDiscontinued() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid month for the discontinued field and click the Submit
		// button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-13-31");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateDayForDiscontinued() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid day for the discontinued field and click the Submit button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-32");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerWithInvalidDateYearForDiscontinued() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Provide an invalid year for the discontinued field and click the Submit
		// button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("-12-32");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Check if there is an error
		Assert.assertTrue(driver.getPageSource().contains("clearfix error"),
				"The clearfix error phrase is not present");
	}

	@Test
	public void addComputerDetailsHitCancelAndSearchComputer() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Fill up the form and click the Cancel button
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-12");
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-12");
		Select dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		dropdown.selectByVisibleText("Thinking Machines");
		driver.findElement(By.linkText("Cancel")).click();
		Thread.sleep(1000);

		// Check if the computer has not been added
		Assert.assertFalse(searchComputer(name), "Error while seaching computer which is not added");
		Assert.assertTrue(driver.getPageSource().contains("Nothing to display"),
				"Error while seaching computer which is not added");
	}

	@Test
	public void checkIfAllTheComputerNamesInTheTableAreInAlphabeticalOrderWhileScrollingForward() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Get the computer names on the first page
		ArrayList<String> names = new ArrayList<String>();
		List<WebElement> namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
		for (WebElement nameEle : namesElements) {
			names.add(nameEle.getText());
		}
		// locate next button
		WebElement nextElement = driver.findElement(By.partialLinkText("Next"));
		WebElement parentElement = nextElement.findElement(By.xpath("./.."));
		String nextButtonClass = parentElement.getAttribute("class");

		// Keep clicking next till it is disabled
		while (!nextButtonClass.contains("disabled")) {
			driver.findElement(By.partialLinkText("Next")).click();
			Thread.sleep(1000);

			// add the computer names from the current page
			namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
			for (WebElement nameElement : namesElements) {
				names.add(nameElement.getText());
			}
			nextElement = driver.findElement(By.partialLinkText("Next"));
			parentElement = nextElement.findElement(By.xpath("./.."));
			nextButtonClass = parentElement.getAttribute("class");
		}

		// Check if the computer names are sorted
		Assert.assertTrue(Utilities.isCollectionSorted(names), "The computer names are not sorted");
	}

	@Test
	public void checkScrollingBack() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		ArrayList<String> namesForward = new ArrayList<String>();
		ArrayList<String> namesBackward = new ArrayList<String>();

		// Get the computer names on the first page
		List<WebElement> namesElementsForward = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
		for (WebElement nameElement : namesElementsForward) {
			namesForward.add(nameElement.getText());
		}

		// locate next button
		WebElement nextElement = driver.findElement(By.partialLinkText("Next"));
		WebElement parentElement = nextElement.findElement(By.xpath("./.."));
		String nextButtonClass = parentElement.getAttribute("class");

		// Keep clicking next till it is disabled
		while (!nextButtonClass.contains("disabled")) {
			driver.findElement(By.partialLinkText("Next")).click();
			Thread.sleep(1000);

			// add the computer names from the current page
			namesElementsForward = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
			for (WebElement nameElement : namesElementsForward) {
				namesForward.add(nameElement.getText());
			}
			nextElement = driver.findElement(By.partialLinkText("Next"));
			parentElement = nextElement.findElement(By.xpath("./.."));
			nextButtonClass = parentElement.getAttribute("class");
		}

		//// add the computer names from the last page
		List<WebElement> namesElementsBackward = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
		for (WebElement nameElement : namesElementsBackward) {
			namesBackward.add(nameElement.getText());
		}

		// locate previous button
		WebElement prevElement = driver.findElement(By.partialLinkText("Previous"));
		WebElement parentPrevElement = prevElement.findElement(By.xpath("./.."));
		String prevButtonClass = parentPrevElement.getAttribute("class");

		// Keep clicking previous till it is disabled
		while (!prevButtonClass.contains("disabled")) {
			driver.findElement(By.partialLinkText("Previous")).click();
			Thread.sleep(1000);

			// add the computer names from the current page
			namesElementsBackward = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
			for (WebElement nameElement : namesElementsBackward) {
				namesBackward.add(nameElement.getText());
			}
			prevElement = driver.findElement(By.partialLinkText("Previous"));
			parentPrevElement = prevElement.findElement(By.xpath("./.."));
			prevButtonClass = parentPrevElement.getAttribute("class");
		}

		// Check if the number of names are the same in the forward and backward lists
		Assert.assertTrue(namesForward.size() == namesBackward.size(), "There is an issue with scrolling back");
	}

	@Test
	public void checkAddAndDelete() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Fill in the info and click Submit
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-12");
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-12");
		Select dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		dropdown.selectByVisibleText("Thinking Machines");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Search for the newly added computer
		driver.findElement(By.id("searchbox")).sendKeys(name);
		driver.findElement(By.id("searchsubmit")).click();
		Thread.sleep(1000);

		// Click the computer name
		driver.findElement(By.linkText(name)).click();
		Thread.sleep(1000);

		// Click the delete button
		driver.findElement(By.className("danger")).click();
		Thread.sleep(1000);

		// Check for messaging and that the computer is no longer available
		Assert.assertTrue(driver.getPageSource().contains("Computer has been deleted"),
				"Error while seaching computer which is not deleted");
		Assert.assertFalse(searchComputer(name), "Error while seaching computer which is not deleted");
	}

	@Test
	public void checkAddAndUpdate() throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Click the Add Computer link
		driver.findElement(By.linkText(properties.getProperty(ADD_COMPUTER_PHRASE_KEY))).click();
		Thread.sleep(1000);

		// Fill in the info and click Submit
		String name = "PTest" + Utilities.getRandom(10000);
		driver.findElement(By.id(properties.getProperty(NAME_KEY))).sendKeys(name);
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("9999-12-12");
		driver.findElement(By.id(properties.getProperty(DISCONTINUED_KEY))).sendKeys("9999-12-12");
		Select dropdown = new Select(driver.findElement(By.id(properties.getProperty(COMPANY_KEY))));
		dropdown.selectByVisibleText("Thinking Machines");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Search for the newly added computer
		driver.findElement(By.id("searchbox")).sendKeys(name);
		driver.findElement(By.id("searchsubmit")).click();
		Thread.sleep(1000);

		// Click the computer name
		driver.findElement(By.linkText(name)).click();
		Thread.sleep(1000);

		// Update the info and click Submit
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).clear();
		driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).sendKeys("8888-12-12");
		driver.findElement(By.className("primary")).click();
		Thread.sleep(1000);

		// Search for the newly updated computer
		driver.findElement(By.id("searchbox")).sendKeys(name);
		driver.findElement(By.id("searchsubmit")).click();

		// Click the computer name
		driver.findElement(By.linkText(name)).click();
		Thread.sleep(1000);

		// Check if the computer details have been updated
		String updatedInfo = driver.findElement(By.id(properties.getProperty(INTRODUCED_KEY))).getAttribute("value");
		Assert.assertTrue(updatedInfo.equals("8888-12-12"));
	}

	private boolean checkIfSpecificRowIsInTable(final String computerName) throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Get the computer names on the first page
		List<String> names = new ArrayList<String>();
		List<WebElement> namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
		for (WebElement nameEle : namesElements) {
			if (nameEle.getText().equals(computerName)) {
				return true;
			}
			names.add(nameEle.getText());
		}

		// locate next button
		WebElement nextElement = driver.findElement(By.partialLinkText("Next"));
		WebElement parentElement = nextElement.findElement(By.xpath("./.."));
		String nextButtonClass = parentElement.getAttribute("class");

		// Keep clicking next till it is disabled
		while (!nextButtonClass.contains("disabled")) {
			driver.findElement(By.partialLinkText("Next")).click();
			Thread.sleep(1000);
			namesElements = driver.findElements(By.cssSelector("tbody>tr>td:nth-child(1)"));
			for (WebElement nameElement : namesElements) {
				if (nameElement.getText().equals(computerName)) {
					return true;
				}
				names.add(nameElement.getText());
			}
			nextElement = driver.findElement(By.partialLinkText("Next"));
			parentElement = nextElement.findElement(By.xpath("./.."));
			nextButtonClass = parentElement.getAttribute("class");
		}
		return false;
	}

	private boolean searchComputer(final String computerName) throws Exception {
		// Navigate to the URL
		driver.get(properties.getProperty(URL));

		// Search for the computer name
		driver.findElement(By.id("searchbox")).sendKeys(computerName);
		driver.findElement(By.id("searchsubmit")).click();
		try {
			// Get the computer name from the table
			WebElement namesElement = driver.findElement(By.cssSelector("tbody>tr>td:nth-child(1)"));
			if (namesElement.getText().equals(computerName)) {
				return true;
			}
		} catch (NoSuchElementException ex) {
			return false;
		}
		return false;
	}
}
