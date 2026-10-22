package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
@Tag("selenium")
class SeleniumShopTest extends UiApiSupport {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000", "--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void adminCanAddProductAndSeeItOnTheShopPage() {
        String name = "Selenium add " + UUID.randomUUID();
        openAdminAndSignIn(adminUsername(), adminPassword());

        driver.findElement(By.id("n-name")).sendKeys(name);
        driver.findElement(By.id("n-price")).sendKeys("45");
        driver.findElement(By.id("add-btn")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".toast"), "Товар успешно добавлен"));
        trackProductByName(name);

        driver.findElement(By.cssSelector("a[href='/']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#products-list .product-card")));
        assertTrue(driver.findElement(By.cssSelector("#products-list")).getText().contains(name),
                "The product added in admin should appear in the shop list");
    }

    @Test
    void productCanBeAddedToTheCart() {
        long productId = createProduct("Selenium cart " + UUID.randomUUID(), 35);
        driver.get(BASE_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card-" + productId)));
        driver.findElement(By.cssSelector("[data-action='add-to-cart'][data-id='" + productId + "']")).click();
        driver.findElement(By.id("open-cart-btn")).click();

        String itemSelector = "#cart-item-" + productId;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(itemSelector)));
        assertTrue(driver.findElement(By.cssSelector(itemSelector)).getText().contains("Selenium cart"),
                "The selected product should be visible in the cart");
    }

    @Test
    void adminRejectsAnIncorrectLogin() {
        driver.get(BASE_URL + "/admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys("wrong-user");
        driver.findElement(By.id("password")).sendKeys("wrong-password");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error")));
        assertTrue(driver.findElement(By.cssSelector(".error")).isDisplayed(),
                "An error message should be shown after an incorrect login");
    }

    @Test
    void cartItemsRemainAfterRefreshingThePage() {
        long productId = createProduct("Selenium refresh " + UUID.randomUUID(), 25);
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card-" + productId)));
        driver.findElement(By.cssSelector("[data-action='add-to-cart'][data-id='" + productId + "']")).click();
        driver.findElement(By.id("open-cart-btn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-item-" + productId)));

        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card-" + productId)));
        driver.findElement(By.id("open-cart-btn")).click();

        wait.until(d -> "1".equals(d.findElement(By.id("cart-count")).getText()));
        assertTrue(driver.findElement(By.id("cart-item-" + productId)).isDisplayed(),
                "The cart item should remain after a page refresh");
    }

    private void openAdminAndSignIn(String username, String password) {
        driver.get(BASE_URL + "/admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
    }
}
