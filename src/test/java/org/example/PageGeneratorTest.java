package org.example;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PageGeneratorTest {
    @Test
    public void testGenerateHomePage() {
        String html = PageGenerator.generateHomePage();
        assertTrue(html.contains("<h1>Welcome to the Sports Betting Simulation</h1>"));
        assertTrue(html.contains("<li><a href='/bettor'>Bettor</a></li>"));
        assertTrue(html.contains("<li><a href='/simulation-user'>Simulation User</a></li>"));
        assertTrue(html.contains("<li><a href='/bookmaker'>Bookmaker</a></li>"));
    }

    @Test
    public void testGenerateBettorPage() {
        String html = PageGenerator.generateBettorPage();
        assertTrue(html.contains("<h1>Bettor Page</h1>"));
        assertTrue(html.contains("<form method='POST' action='/bettor'>"));
        assertTrue(html.contains("<input type='submit' value='Place Bet'>"));
    }

    /// Failing Tests
    /*
    @Test
    public void testFailingGenerateHomePage() {
        String html = PageGenerator.generateHomePage();
        assertTrue(html.contains("<h1>Incorrect Title</h1>")); // This will fail because the expected content is incorrect
    }

    @Test
    public void testFailingGenerateBettorPage() {
        String html = PageGenerator.generateBettorPage();
        assertTrue(html.contains("<h1>Incorrect Page</h1>")); // This will fail because the expected content is incorrect
    }

     */
}
///PASSED
/*Test the HTML generation methods to ensure they return the expected content.
Validate the presence of specific elements in the generated HTML (e.g., form fields, links).
 */

///FAILED
/*PageGeneratorTest.java: Both tests in this file will fail because
the expected HTML content is incorrect ("<h1>Incorrect Title</h1>" and "<h1>Incorrect Page</h1>").
 */