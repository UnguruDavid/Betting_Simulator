package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class BetTest {
    @Test
    public void testBetConstructor() {
        Bet bet = new Bet("Bookmaker", "TeamA-TeamB", "2.0", "3.0", "4.0", 100.0);
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
    public void testFailingBetConstructor() {
        Bet bet = new Bet("Bookmaker", "TeamA-TeamB", "2.0", "3.0", "4.0", 100.0);
        assertEquals("WrongName", bet.name); // This will fail because the expected value is incorrect
    }

     */
}
///PASSED
/*Test the constructor to ensure that it initializes all fields correctly.
Test getters and setters (if any).
 */

///FAILED
/* BetTest.java: The testBetConstructor test will fail because the
expected value "WrongName" does not match the actual value "Bookmaker".
*/