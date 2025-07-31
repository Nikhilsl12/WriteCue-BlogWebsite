package org.coderscrib.blogapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for demonstrating and testing the custom exception handling.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(ExceptionHandlingTest.TestExceptionController.class)
public class ExceptionHandlingTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Test controller for triggering exceptions.
     */
    @RestController
    @RequestMapping("/test-exceptions")
    public static class TestExceptionController {

        @GetMapping("/resource-not-found")
        public void throwResourceNotFoundException() {
            throw ResourceNotFoundException.create("TestResource", "id", 1);
        }

        @GetMapping("/bad-request")
        public void throwBadRequestException() {
            throw new BadRequestException("Invalid request parameters");
        }

        @GetMapping("/conflict")
        public void throwConflictException() {
            throw new ConflictException("Operation cannot be performed due to conflict");
        }
    }

    @Test
    public void testResourceNotFoundException() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/test-exceptions/resource-not-found", Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("TestResource not found with id: '1'", body.get("message"));
        assertEquals(404, body.get("status"));
    }

    @Test
    public void testBadRequestException() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/test-exceptions/bad-request", Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid request parameters", body.get("message"));
        assertEquals(400, body.get("status"));
    }

    @Test
    public void testConflictException() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/test-exceptions/conflict", Map.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Operation cannot be performed due to conflict", body.get("message"));
        assertEquals(409, body.get("status"));
    }
}
