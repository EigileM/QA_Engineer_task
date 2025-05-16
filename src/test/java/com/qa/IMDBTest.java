package com.qa;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import org.testng.annotations.Test;

public class IMDBTest {

    @Test
    public void imdbRegressionTest() {
        // 1. Open www.imdb.com
        open("https://www.imdb.com");

        // 2. Search for "QA" with the search bar
        $("#suggestion-search").setValue("QA");

        // 3. Save the name of the first title
        // Wait for dropdown to be visible
        $("div#react-autowhatever-navSuggestionSearch").shouldBe(visible);

        // Get the first suggestion's title from the dropdown and print title to the console
        String firstTitleName = $$("div#react-autowhatever-navSuggestionSearch .searchResult__constTitle")
                .first()
                .shouldBe(visible)
                .getText();
        System.out.println("First suggested title: " + firstTitleName);

        // 4. Click the first suggestion
        $$("div#react-autowhatever-navSuggestionSearch div a").first().click();
        // Wait for the page to load
        $("body").shouldBe(visible);

        // 5. Verify that page title matches the one saved from the dropdown and print title to the console
        SelenideElement pageTitle = $("h1").shouldBe(visible);
        String actualTitleName = pageTitle.getText();
        assert actualTitleName.equalsIgnoreCase(firstTitleName) :
                "Expected title '" + firstTitleName + "', but found '" + actualTitleName + "'";
        System.out.println("Verified title page matches: " + actualTitleName);

        // 6. Verify there are more than 3 members in the "Top Cast" section
        String badgeText = $(".ipc-title__subtext").shouldBe(visible).getText();

        // Convert "99+" to a number
        int count;
        if (badgeText.contains("+")) {
            count = Integer.parseInt(badgeText.replace("+", ""));
        } else {
            count = Integer.parseInt(badgeText);
        }

        // Assert count is more than 3
        assert count > 3 : "Expected count to be greater than 3, but was " + count;

        // 7. Click on the third profile in the "Top Cast" section
        SelenideElement castSection = $("section[data-testid='title-cast']");
        castSection.scrollIntoView(true);
        castSection.shouldBe(visible);
        ElementsCollection castItems = $$("div[data-testid='title-cast-item']");
        castItems.shouldHave(sizeGreaterThan(2));
        SelenideElement thirdCastItem = castItems.get(2);
        thirdCastItem.scrollIntoView(true);
        thirdCastItem.shouldBe(visible);

        // 8. Verify that correct profile have opened
        String expectedActorName = thirdCastItem.$("a[data-testid='title-cast-item__actor']")
                .shouldBe(visible).getText();
        thirdCastItem.scrollIntoView(true);
        thirdCastItem.$("a[data-testid='title-cast-item__actor']").click();
        String currentUrl = WebDriverRunner.url();
        assert currentUrl.contains("/name/") : "Expected actor profile URL but got: " + currentUrl;

        // Verify actor name is displayed in the header
        SelenideElement actorNameHeader = $("h1").shouldBe(visible);
        String actualActorName = actorNameHeader.getText();
        assert actualActorName.toLowerCase().contains(expectedActorName.toLowerCase()) :
                "Expected actor name '" + expectedActorName + "' but found '" + actualActorName + "'";
    }
}

