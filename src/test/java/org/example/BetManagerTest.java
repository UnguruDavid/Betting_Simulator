package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BetManagerTest {
    private BetManager betManager;

    @BeforeEach
    public void setUp() {
        betManager = new BetManager();
    }

    @Test
    public void testHandleBookmakerPost() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        String requestBody = "name=Bookmaker&event=TeamA-TeamB&oddsA=2.0&oddsDraw=3.0&oddsB=4.0&maxSum=100.0";

        BetManager.handleBookmakerPost(requestBody, out);
        String response = sw.toString();

        assertEquals("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nBet received successfully!", response.trim());

        Map<String, Bet> offeredBets = BetManager.getOfferedBets();
        assertEquals(1, offeredBets.size());
        Bet bet = offeredBets.values().iterator().next();
        assertEquals("Bookmaker", bet.name);
        assertEquals("TeamA-TeamB", bet.event);
        assertEquals("2.0", bet.oddsA);
        assertEquals("3.0", bet.oddsDraw);
        assertEquals("4.0", bet.oddsB);
        assertEquals(100.0, bet.maxSum);
    }

    /// Failing Test
    /*
    @Test
    public void testFailingHandleBookmakerPost() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        String requestBody = "name=Bookmaker&event=TeamA-TeamB&oddsA=2.0&oddsDraw=3.0&oddsB=4.0&maxSum=100.0";

        BetManager.handleBookmakerPost(requestBody, out);
        String response = sw.toString();

        assertEquals("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nBet received successfully!", response.trim()); // This will fail because the expected response is incorrect

        Map<String, Bet> offeredBets = BetManager.getOfferedBets();
        assertEquals(1, offeredBets.size());
        Bet bet = offeredBets.values().iterator().next();
        assertEquals("WrongName", bet.name); // This will fail because the expected value is incorrect
    }

     */
}
///PASSED
/*Test the handleBookmakerPost method with valid and invalid inputs.
Test the handleBettorPost method with valid and invalid inputs.
Test the handleCheckOutcomePost method with different scenarios.
Test getMatchingBetKey and getOddsByOutcome methods.
 */

 ///FAIL
/*BetManagerTest.java: The testHandleBookmakerPost test will fail because
the expected response "HTTP/1.1 400 Bad Request\r\nContent-Type:
text/plain\r\n\r\nBet received successfully!" is incorrect. Additionally,
 the check for the bet's name "WrongName" will fail because
 the actual name is"Bookmaker".
 */