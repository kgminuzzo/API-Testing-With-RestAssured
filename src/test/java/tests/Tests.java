package tests;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(DataProviderRunner.class)
public class Tests {

    private static RequestSpecification requestSpec;
    private static ResponseSpecification responseSpec;

    @BeforeClass
    public static void createSpecifications() {
        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://api.zippopotam.us").
                build();
        responseSpec = new ResponseSpecBuilder().
                expectStatusCode(200).
                expectContentType(ContentType.JSON).
                build();
    }

    @DataProvider
    public static Object[][] zipCodesAndPlaces() {
        return new Object[][] {
                { "us", "90210", "Beverly Hills" },
                { "us", "12345", "Schenectady" },
                { "ca", "B2R", "Waverley"}
        };
    }

    @Test
    @UseDataProvider("zipCodesAndPlaces")
    public void requestZipCodesFromCollection_checkPlaceNameInResponseBody_expectSpecifiedPlaceName(String countryCode, String zipCode, String expectedPlaceName) {
        String placeName = given().
                spec(requestSpec).
                pathParam("countryCode", countryCode).pathParam("zipCode", zipCode).
                when().
                get("{countryCode}/{zipCode}").
                then().
                spec(responseSpec).
                assertThat().
                body("places[0].'place name'", equalTo(expectedPlaceName)).
                extract().
                path("places[0].'place name'");

        Assert.assertEquals(expectedPlaceName,placeName);
    }

    @Test
    public void requestUsZipCode90210_checkPlaceNameInResponseBody_expectBeverlyHills() {

        given().
                when().
                get("http://zippopotam.us/us/90210").
                then().
                assertThat().
                body("places[0].'place name'", equalTo("Beverly Hills"));
    }

    @Test
    public void requestUsZipCode12345_checkPlaceNameInResponseBody_expectSchenectady() {

        given().
                when().
                get("http://zippopotam.us/us/12345").
                then().
                assertThat().
                body("places[0].'place name'", equalTo("Schenectady"));
    }

    @Test
    public void requestCaZipCodeB2R_checkPlaceNameInResponseBody_expectWaverley() {

        given().
                when().
                get("http://zippopotam.us/ca/B2R").
                then().
                assertThat().
                body("places[0].'place name'", equalTo("Waverley"));
    }
}