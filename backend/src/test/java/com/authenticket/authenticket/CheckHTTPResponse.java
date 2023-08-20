package com.authenticket.authenticket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

// annotated to start web server on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckHTTPResponse {
    // Get port number
    @LocalServerPort
    private int port;

    //@SpringBootTest means we can use @Autowired to get a TestRestTemplate
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldPassIfStringMatches() {
        //Compares string in controller
        assertEquals("Hello world", testRestTemplate.getForObject("http://localhost:" + port + "/", String.class));
    }
}
