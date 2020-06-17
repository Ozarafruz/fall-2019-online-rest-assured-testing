package com.automation.test.day4;


import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;


public class ORDSTestsDay4 {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("ORDS.URI");

    }

    /**
     * Warm-up!
     * Given accept type is JSON
     * When users sends a GET request to "/employees"
     * Then status code is 200
     * And Content type is application/json
     * And response time is less than 3 seconds
     */

    @Test
    @DisplayName("Verify status code, content type and response time")
    public void employeesTest1() {

        given().accept(ContentType.JSON)
                .when().get("/employees").prettyPeek()
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .time(lessThan(3L), TimeUnit.SECONDS);
    }

    /**
     * Given accept type is JSON
     * And parameters: q = {"country_id":"US"}
     * When users sends a GET request to "/countries"
     * Then status code is 200
     * And Content type is application/json
     */
    @Test
    @DisplayName("Verify country name, content type and status code for country with ID US")
    public void verifyCountriesTest1() {
        given().accept(ContentType.JSON)
                .queryParam("q", "{\"country_id\":\"US\"}")
                .when().get("/countries")
                //    .prettyPeek()
                .then().assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("items[0].country_name", is("United States of America"));

        //Second request
        //accept(ContentType.JSON) - to request  JSON from the web service.
        Response response = given().accept(ContentType.JSON)
                .when().get("/countries")
                .prettyPeek();

        String countryName = response.jsonPath().getString("items.find {it.country_id == 'US'}.country_name");
        Map<String, Object> countryUS = response.jsonPath().get("items.find{it.country_id == 'US'}");
        //find all country names from region 2
        //collectionName.findAll{it.propertyName =='Value'} -- to get collection objects where property equals to some value
        //collectionName.findAll{it.propertyName =='Value'} -- to object where property equals to some value

        //To get collection properties where property equals to some value
        //collectionName.findAll{it.propertyName == 'Value'}.propertyName
        List<String> countryNames = response.jsonPath().getList("items.findAll{it.region_id == 2}.country_name");

        System.out.println("Country Name = " + countryName);
        System.out.println("Country US = " + countryUS);
        System.out.println("Country Names = " + countryNames);

        for (Map.Entry<String, Object> entry : countryUS.entrySet()) {
            System.out.printf("key = %s, value = %s\n", entry.getKey(), entry.getValue());
        }

    }

    //let's find employee with highest salary. Use GPath
    @Test
    @DisplayName("")
    public void getEmployeeTest() {
        Response response = when().get("/employees").prettyPeek();

        Map<String, ?> bestEmployee = response.jsonPath().get("items.max{it.salary}");
        Map<String, ?> poorGuy = response.jsonPath().get("items.min{it.salary}");

        int companiesPayroll = response.jsonPath().get("items.collect{it.salary}.sum()");

        System.out.println("bestEmployee = " + bestEmployee);
        System.out.println("poorGuy = " + poorGuy);
        System.out.println("companiesPayroll = " + companiesPayroll);

    }

    @Test
    @DisplayName("Verify that every employee has positive salary")
    public void testSalary() {
        when().get("/employees")
                .then().assertThat()
                .statusCode(200)
                .body("items.salary", everyItem(greaterThan(0)))
                .log().ifError();
    }

    @Test
    public void verifyPhoneNUmber() {


    Response response = when().get("/employees/{id}", 101).prettyPeek();
    response.then().assertThat().statusCode(200);

    String expected = "515-123-4568";
    String actual = response.jsonPath().getString("phone number").replace(".", "-");

    assertEquals(200, response.getStatusCode());
    assertEquals(expected,actual);

}
}
