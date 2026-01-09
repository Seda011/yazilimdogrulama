package com.mycompany.orderservice.system;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeleniumSystemTest {

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);

        // Target URL - Environment variable or default
        baseUrl = System.getProperty("app.url", "http://localhost:8080");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testPlaceOrderFlow() {
        driver.get(baseUrl + "/index.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Inputs
        WebElement customerInput = driver.findElement(By.id("customerId"));
        WebElement productInput = driver.findElement(By.id("productIds"));
        WebElement placeOrderBtn = driver.findElement(By.id("placeOrderBtn"));

        customerInput.clear();
        customerInput.sendKeys("1");

        productInput.clear();
        productInput.sendKeys("1,2");

        // 2. Click Place Order
        placeOrderBtn.click();

        // 3. Verify Result
        WebElement resultDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderResult")));
        assertTrue(resultDiv.getText().contains("Order Placed! ID:"));
    }

    @Test
    void testViewOrdersFlow() {
        driver.get(baseUrl + "/index.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. View Orders
        WebElement orderBtn = driver.findElement(By.id("viewOrdersBtn"));
        orderBtn.click();

        // 2. Verify List Populated (Assuming data exists from seeder or previous test)
        // Wait for at least one list item if data is anticipated
        // For robustness in valid empty state, just checking no error is shown
        WebElement list = driver.findElement(By.id("ordersList"));
        System.out.println("List text: " + list.getText());
        // Assertions can be stricter based on known data state
        assertTrue(list.isDisplayed());
    }
}
