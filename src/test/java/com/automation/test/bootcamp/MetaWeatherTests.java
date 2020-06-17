package com.automation.test.bootcamp;

import com.google.common.io.Files;
import io.restassured.response.Response;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MetaWeatherTests {
    private final static Logger logger = LogManager.getLogger(MetaWeatherTests.class);

    @BeforeAll
    public static void beforeAll() {
        baseURI = "https://www.metaweather.com/api";
        logger.info("Using base URI: https://www.metaweather.com/api");
    }

    @Test
    public void getCityTest() {
        String city = "New York";
        // we need to use accept() only if web service returns different content
        Response response = given().
                queryParam("query", city).
                when().
                get("/location/search").prettyPeek();
        //we extract body, headers for verification
//        [city], "city", [] means it's array, it fails because we are comparing array with string
        response.then().assertThat().body("[0].title", is(city));
//        to extract value from JSON body we use jsonpath, it's like xpath for JSON
//        $.[0], in rest assured we are skipping $.
//        since it's array, [index] is used to get specific object
//        this is an JSON object, on the left side we have properties, on the right - values.
//        JSON supports different data types like string, integer, boolean
//        also it has collections []
//        {
//            "title": "New York",
//                "location_type": "City",
//                "woeid": 2459115,
//                "latt_long": "40.71455,-74.007118"
//        }
//        in rest assured, then() part is used for verifications
//        we use hamcrest matcher for assertions
        int woeid = response.jsonPath().getInt("[0].woeid");

//      {woeid} - path parameter
//        put value after ,
//        path parameters must be inside {}
        Response response2 = get("/location/{woeid}", woeid).prettyPeek();
//        title - json path to get value of property "title"
        response2.then().statusCode(200).body("title", is(city));
    }
}
