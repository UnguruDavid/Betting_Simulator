package org.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.BettingProto.*;

public class BettingClient {

    public static void main(String[] args) {
        // Create a channel to connect to the gRPC server on localhost at port 8080
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        // Create a blocking stub to make synchronous RPC calls
        BettingServiceGrpc.BettingServiceBlockingStub stub = BettingServiceGrpc.newBlockingStub(channel);

        // Offer Bet by Bookmaker1
        BetRequest offerRequest1 = BetRequest.newBuilder()
                .setName("Bookmaker1")
                .setEvent("TeamA-TeamB")
                .setOddsA("2.0")
                .setOddsDraw("3.0")
                .setOddsB("4.0")
                .setMaxSum(1000)
                .build();
        BetResponse offerResponse1 = stub.offerBet(offerRequest1);
        System.out.println(offerResponse1.getMessage());

        // Offer Bet by Bookmaker2
        BetRequest offerRequest2 = BetRequest.newBuilder()
                .setName("Bookmaker2")
                .setEvent("TeamC-TeamD")
                .setOddsA("1.5")
                .setOddsDraw("2.5")
                .setOddsB("3.5")
                .setMaxSum(2000)
                .build();
        BetResponse offerResponse2 = stub.offerBet(offerRequest2);
        System.out.println(offerResponse2.getMessage());

        // List all Bets
        Empty listRequest = Empty.newBuilder().build();
        ListBetsResponse listResponse = stub.listBets(listRequest);
        System.out.println("Listing all bets:");
        listResponse.getBetsList().forEach(bet -> {
            System.out.println("Bet: " + bet.getEvent() + " offered by " + bet.getName() +
                    " with odds (A: " + bet.getOddsA() + ", Draw: " + bet.getOddsDraw() + ", B: " + bet.getOddsB() + ")" +
                    " and max sum: " + bet.getMaxSum());
        });

        // Remove Bet by Bookmaker1 for TeamA-TeamB
        RemoveBetRequest removeRequest1 = RemoveBetRequest.newBuilder().setEvent("TeamA-TeamB").build();
        BetResponse removeResponse1 = stub.removeBet(removeRequest1);
        System.out.println(removeResponse1.getMessage());

        // Remove Bet by Bookmaker2 for TeamC-TeamD
        RemoveBetRequest removeRequest2 = RemoveBetRequest.newBuilder().setEvent("TeamC-TeamD").build();
        BetResponse removeResponse2 = stub.removeBet(removeRequest2);
        System.out.println(removeResponse2.getMessage());

        // List all Bets again to see the changes
        listResponse = stub.listBets(listRequest);
        System.out.println("Listing all bets after removals:");
        if (listResponse.getBetsList().isEmpty()) {
            System.out.println("No bets available.");
        } else {
            listResponse.getBetsList().forEach(bet -> {
                System.out.println("Bet: " + bet.getEvent() + " offered by " + bet.getName() +
                        " with odds (A: " + bet.getOddsA() + ", Draw: " + bet.getOddsDraw() + ", B: " + bet.getOddsB() + ")" +
                        " and max sum: " + bet.getMaxSum());
            });
        }

        // Shut down the channel
        channel.shutdown();
    }
}
/*
Explanation
Establishing the Channel:

java
Copy code
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext()
        .build();
This line creates a channel to connect to the gRPC server running on localhost at port 8080.
Creating the Blocking Stub:

BettingServiceGrpc.BettingServiceBlockingStub stub = BettingServiceGrpc.newBlockingStub(channel);
This creates a blocking stub to make synchronous RPC calls to the server.
Offering Bets by Multiple Bookmakers:

// Offer Bet by Bookmaker1
BetRequest offerRequest1 = BetRequest.newBuilder()
        .setName("Bookmaker1")
        .setEvent("TeamA-TeamB")
        .setOddsA("2.0")
        .setOddsDraw("3.0")
        .setOddsB("4.0")
        .setMaxSum(1000)
        .build();
BetResponse offerResponse1 = stub.offerBet(offerRequest1);
System.out.println(offerResponse1.getMessage());

// Offer Bet by Bookmaker2
BetRequest offerRequest2 = BetRequest.newBuilder()
        .setName("Bookmaker2")
        .setEvent("TeamC-TeamD")
        .setOddsA("1.5")
        .setOddsDraw("2.5")
        .setOddsB("3.5")
        .setMaxSum(2000)
        .build();
BetResponse offerResponse2 = stub.offerBet(offerRequest2);
System.out.println(offerResponse2.getMessage());
Two different bookmakers offer bets on different events.
Listing All Bets:

Empty listRequest = Empty.newBuilder().build();
ListBetsResponse listResponse = stub.listBets(listRequest);
System.out.println("Listing all bets:");
listResponse.getBetsList().forEach(bet -> {
    System.out.println("Bet: " + bet.getEvent() + " offered by " + bet.getName() +
                       " with odds (A: " + bet.getOddsA() + ", Draw: " + bet.getOddsDraw() + ", B: " + bet.getOddsB() + ")" +
                       " and max sum: " + bet.getMaxSum());
});
Lists all bets offered by the bookmakers, printing details of each bet.
Removing Bets:

// Remove Bet by Bookmaker1 for TeamA-TeamB
RemoveBetRequest removeRequest1 = RemoveBetRequest.newBuilder().setEvent("TeamA-TeamB").build();
BetResponse removeResponse1 = stub.removeBet(removeRequest1);
System.out.println(removeResponse1.getMessage());

// Remove Bet by Bookmaker2 for TeamC-TeamD
RemoveBetRequest removeRequest2 = RemoveBetRequest.newBuilder().setEvent("TeamC-TeamD").build();
BetResponse removeResponse2 = stub.removeBet(removeRequest2);
System.out.println(removeResponse2.getMessage());
Removes the bets offered by Bookmaker1 and Bookmaker2.
Listing All Bets After Removal:

listResponse = stub.listBets(listRequest);
System.out.println("Listing all bets after removals:");
if (listResponse.getBetsList().isEmpty()) {
    System.out.println("No bets available.");
} else {
    listResponse.getBetsList().forEach(bet -> {
        System.out.println("Bet: " + bet.getEvent() + " offered by " + bet.getName() +
                           " with odds (A: " + bet.getOddsA() + ", Draw: " + bet.getOddsDraw() + ", B: " + bet.getOddsB() + ")" +
                           " and max sum: " + bet.getMaxSum());
    });
}
Lists all bets again to verify that the previously added bets have been removed.
Shutting Down the Channel:

channel.shutdown();
Closes the gRPC channel.
 */