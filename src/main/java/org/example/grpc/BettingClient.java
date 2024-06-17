package org.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.BettingProto.*;

public class BettingClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        BettingServiceGrpc.BettingServiceBlockingStub stub = BettingServiceGrpc.newBlockingStub(channel);

        // Offer Bet
        BetRequest offerRequest = BetRequest.newBuilder()
                .setName("Bookmaker1")
                .setEvent("TeamA-TeamB")
                .setOddsA("2.0")
                .setOddsDraw("3.0")
                .setOddsB("4.0")
                .setMaxSum(1000)
                .build();

        BetResponse offerResponse = stub.offerBet(offerRequest);
        System.out.println(offerResponse.getMessage());

        // Place Bet
        PlaceBetRequest placeRequest = PlaceBetRequest.newBuilder()
                .setBettorName("Bettor1")
                .setEvent("TeamA-TeamB")
                .setOutcome("1")
                .setAmount("500")
                .build();

        PlaceBetResponse placeResponse = stub.placeBet(placeRequest);
        System.out.println(placeResponse.getMessage());

        // Check Outcome
        CheckOutcomeRequest checkRequest = CheckOutcomeRequest.newBuilder()
                .setEvent("TeamA-TeamB")
                .setOutcome("1")
                .setAmount("500")
                .build();

        CheckOutcomeResponse checkResponse = stub.checkOutcome(checkRequest);
        System.out.println(checkResponse.getMessage());

        channel.shutdown();
    }
}
