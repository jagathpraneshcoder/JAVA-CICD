package com.visualpathit.account;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.Assert.assertTrue;

public class SeleniumHeadlessTest {

    @Test
    public void testHomePageTitle() {
        // Set Chrome options for headless run
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        // Initialize driver
        WebDriver driver = new ChromeDriver(options);
        driver.get("http://localhost:8080/vprofile/"); // change if your app runs elsewhere

        String title = driver.getTitle();
        System.out.println("✅ Page Title: " + title);

        assertTrue("Home page title should not be empty", title != null && !title.isEmpty());
        driver.quit();
    }
}
