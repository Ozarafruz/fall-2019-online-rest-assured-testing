package com.automation.test.day9;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class BadSSL {

    @Test
    public void badSSLCertificateTest(){
        /**
         * no valid certificate - no handshake, no secure connection
         */
        baseURI = "https://untrusted-root.badssl.com/"; //fake SSL certificate
        Response response = get().prettyPeek();

    }
}
