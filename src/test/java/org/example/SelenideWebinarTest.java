package org.example;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

@Tag("ui")
@Tag("webinar")
class SelenideWebinarTest extends UiApiSupport {

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
    void threeItemsCanBeOrderedForNoMoreThanThreeHundredRubles() {
        long productId = createProduct("Webinar order " + UUID.randomUUID(), 80);
        open("/");
        $("#card-" + productId).shouldBe(visible);
        $("#q-" + productId).setValue("3");
        $("[data-action='add-to-cart'][data-id='" + productId + "']").click();
        $("#open-cart-btn").click();

        $("#cart-count").shouldHave(exactText("3"));
        $("#total-price").shouldHave(exactText("240"));
        $("#makeOrder").shouldBe(enabled).click();
        $("#main-title").shouldHave(exactText("Заказ успешно оформлен!"));
        $(".toast").shouldBe(visible).shouldHave(text("Заказ принят в обработку!"));
    }

    @Test
    void totalPriceIsCorrectForSeveralDifferentProducts() {
        long firstId = createProduct("Webinar product A " + UUID.randomUUID(), 40);
        long secondId = createProduct("Webinar product B " + UUID.randomUUID(), 55);
        long thirdId = createProduct("Webinar product C " + UUID.randomUUID(), 70);
        open("/");

        $("#card-" + firstId).shouldBe(visible);
        $("[data-action='add-to-cart'][data-id='" + firstId + "']").click();
        $("[data-action='add-to-cart'][data-id='" + secondId + "']").click();
        $("[data-action='add-to-cart'][data-id='" + thirdId + "']").click();
        $("#open-cart-btn").click();

        $$("#cart-items .cart-item").shouldHave(size(3));
        $("#total-price").shouldHave(exactText("165"));
    }

    @Test
    void adminCanAddProductAndSeeTheSuccessNotification() {
        String name = "Webinar admin add " + UUID.randomUUID();
        signInToAdmin();

        $("#n-name").setValue(name);
        $("#n-price").setValue("60");
        $("#add-btn").shouldBe(enabled).click();

        $(".toast").shouldBe(visible).shouldHave(text("Товар успешно добавлен"));
        $("#tbody").shouldHave(text(name));
        trackProductByName(name);
    }

    @Test
    void adminCanEditProductAndChangesAppearOnTheShopPage() {
        long productId = createProduct("Webinar before edit " + UUID.randomUUID(), 20);
        String updatedName = "Webinar after edit " + UUID.randomUUID();
        open("/admin");
        enterCredentials();

        $("#nm-" + productId).shouldBe(visible).setValue(updatedName);
        $("#pr-" + productId).setValue("75");
        $("button[data-action='update'][data-id='" + productId + "']").shouldBe(enabled).click();
        $(".toast").shouldBe(visible).shouldHave(text("обновлен"));
        $("a[href='/']").click();

        $("#card-" + productId + " h4").shouldHave(exactText(updatedName));
        $("#card-" + productId + " > div[style*='font-weight:bold']").shouldHave(text("75 ₽"));
    }

    private void signInToAdmin() {
        open("/admin");
        enterCredentials();
        $("h1").shouldBe(visible).shouldHave(text("SmartShop Admin"));
    }

    private void enterCredentials() {
        $("#username").shouldBe(visible).setValue(adminUsername());
        $("#password").setValue(adminPassword());
        $("button[type='submit']").shouldBe(enabled).click();
    }
}
