package org.example;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Tag("ui")
@Tag("selenide")
class SelenideShopTest extends UiApiSupport {

    @BeforeEach
    void configureBrowser() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.browserSize = "1440x1000";
        Configuration.baseUrl = BASE_URL;
        Configuration.timeout = 8000;
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
    }

    @Test
    void adminCanAddProductAndSeeItOnTheShopPage() {
        String name = "Selenide add " + UUID.randomUUID();
        signInToAdmin(adminUsername(), adminPassword());

        $("#n-name").setValue(name);
        $("#n-price").setValue("45");
        $("#add-btn").shouldBe(enabled).click();
        $(".toast").shouldBe(visible, text("Товар успешно добавлен"));
        trackProductByName(name);

        $("a[href='/']").click();
        $("#products-list").shouldBe(visible, text(name));
    }

    @Test
    void productCanBeAddedToTheCart() {
        long productId = createProduct("Selenide cart " + UUID.randomUUID(), 35);
        open("/");

        $("#card-" + productId).shouldBe(visible);
        $("[data-action='add-to-cart'][data-id='" + productId + "']").shouldBe(enabled).click();
        $("#open-cart-btn").click();
        $("#cart-item-" + productId).shouldBe(visible, text("Selenide cart"));
        $("#cart-count").shouldBe(exactText("1"));
    }

    @Test
    void adminRejectsAnIncorrectLogin() {
        open("/admin");
        $("#username").shouldBe(visible).setValue("wrong-user");
        $("#password").setValue("wrong-password");
        $("button[type='submit']").shouldBe(enabled).click();

        $(".alert-danger").shouldBe(visible, text("Неверные учетные данные пользователя"));
    }

    @Test
    void cartItemsRemainAfterRefreshingThePage() {
        long productId = createProduct("Selenide refresh " + UUID.randomUUID(), 25);
        open("/");
        $("#card-" + productId).shouldBe(visible);
        $("[data-action='add-to-cart'][data-id='" + productId + "']").click();
        $("#open-cart-btn").click();
        $("#cart-item-" + productId).shouldBe(visible);

        Selenide.refresh();
        $("#card-" + productId).shouldBe(visible);
        $("#open-cart-btn").click();
        $("#cart-count").shouldBe(exactText("1"));
        $("#cart-item-" + productId).shouldBe(visible);
        $("#total-price").shouldBe(exactText("25"));
    }

    @Test
    void orderOverThreeHundredShowsAJavaScriptAlert() {
        long firstId = createProduct("Selenide expensive A " + UUID.randomUUID(), 180);
        long secondId = createProduct("Selenide expensive B " + UUID.randomUUID(), 150);
        open("/");

        $("#card-" + firstId).shouldBe(visible);
        $("[data-action='add-to-cart'][data-id='" + firstId + "']").click();
        $("[data-action='add-to-cart'][data-id='" + secondId + "']").click();
        $("#open-cart-btn").click();
        $("#total-price").shouldBe(exactText("330"));
        $("#makeOrder").shouldBe(enabled).click();

        // Selenide waits for and accepts the alert; a missing alert fails the test.
        Selenide.confirm("[SmartShop]: Денег не хватает! Сумма 330 ₽ превышает лимит 300 ₽.");
        $("#cartModal").shouldBe(visible);
        $("#cart-item-" + firstId).shouldBe(visible);
    }

    private void signInToAdmin(String username, String password) {
        open("/admin");
        $("#username").shouldBe(visible).setValue(username);
        $("#password").setValue(password);
        $("button[type='submit']").shouldBe(enabled).click();
        $("h1").shouldBe(visible, text("SmartShop Admin"));
    }
}
