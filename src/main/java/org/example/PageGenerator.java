package org.example;

public class PageGenerator {
    public static String generateHomePage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Welcome to the Sports Betting Simulation</h1>"
                + "<ul>"
                + "<li><a href='/bettor'>Bettor</a></li>"
                + "<li><a href='/simulation-user'>Simulation User</a></li>"
                + "<li><a href='/bookmaker'>Bookmaker</a></li>"
                + "</ul>"
                + "</body></html>";
    }

    public static String generateBettorPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Bettor Page</h1>"
                + "<form method='POST' action='/bettor'>"
                + "<label for='bettorName'>Bettor Name:</label><br>"
                + "<input type='text' id='bettorName' name='bettorName'><br><br>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='outcome'>Outcome (0=Draw, 1=TeamA wins, 2=TeamB wins):</label><br>"
                + "<input type='text' id='outcome' name='outcome'><br><br>"
                + "<label for='amount'>Amount:</label><br>"
                + "<input type='text' id='amount' name='amount'><br><br>"
                + "<input type='submit' value='Place Bet'>"
                + "</form>"
                + "<h2>Check Bet Outcome</h2>"
                + "<form method='POST' action='/check-outcome'>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='outcome'>Outcome (0=Draw, 1=TeamA wins, 2=TeamB wins):</label><br>"
                + "<input type='text' id='outcome' name='outcome'><br><br>"
                + "<label for='amount'>Amount:</label><br>"
                + "<input type='text' id='amount' name='amount'><br><br>"
                + "<input type='submit' value='Check Outcome'>"
                + "</form>"
                + "<h2>Available Bets</h2>"
                + "<a href='/view-offered-bets'>View all offered bets</a>"
                + "</body></html>";
    }

    public static String generateSimulationUserPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Simulation User Page</h1>"
                + "<p>This section is for administrators who observe or modify the simulation.</p>"
                + "</body></html>";
    }

    public static String generateBookmakerPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Bookmaker Page</h1>"
                + "<form method='POST' action='/bookmaker'>"
                + "<label for='name'>Bookmaker Name:</label><br>"
                + "<input type='text' id='name' name='name'><br><br>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='oddsA'>Odds for TeamA:</label><br>"
                + "<input type='text' id='oddsA' name='oddsA'><br><br>"
                + "<label for='oddsDraw'>Odds for Draw:</label><br>"
                + "<input type='text' id='oddsDraw' name='oddsDraw'><br><br>"
                + "<label for='oddsB'>Odds for TeamB:</label><br>"
                + "<input type='text' id='oddsB' name='oddsB'><br><br>"
                + "<label for='maxSum'>Maximum Allotted Sum:</label><br>"
                + "<input type='text' id='maxSum' name='maxSum'><br><br>"
                + "<input type='submit' value='Submit'>"
                + "</form>"
                + "</body></html>";
    }

    public static String viewOfferedBets() {
        StringBuilder response = new StringBuilder("HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Offered Bets</h1>"
                + "<ul>");
        for (Bet bet : BetManager.getOfferedBets().values()) {
            response.append("<li>").append("Event: ").append(bet.event)
                    .append(", Odds: [")
                    .append(bet.oddsA).append(", ")
                    .append(bet.oddsDraw).append(", ")
                    .append(bet.oddsB).append("], Max Sum: ")
                    .append(bet.maxSum).append("</li>");
        }
        response.append("</ul>")
                .append("</body></html>");
        return response.toString();
    }
}
