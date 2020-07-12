package tests;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dataEntities.Location;
import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;

public class TestDeSerialization {

   @Rule
   public WireMockRule wireMockRule = new WireMockRule(options().port(9876));

    @Test
    public void requestUsZipCode90210_checkPlaceNameInResponseBody_expectBeverlyHills() {

        Location location =

                given().
                        when().
                        get("http://api.zippopotam.us/us/90210").
                        as(Location.class);

        Assert.assertEquals(
                "Beverly Hills",
                location.getPlaces().get(0).getPlaceName()
        );
    }

    @Test
    public void sendLvZipCode1050_checkStatusCode_expect200() {

        Location location = new Location();
        location.setCountry("Netherlands");

        given().log().all().
                contentType(ContentType.JSON).
                body(location).
                log().body().
                when().log().all().
                post("http://api.zippopotam.us/lv/1050").
                then().log().all().
                assertThat().
                statusCode(200);
    }
}