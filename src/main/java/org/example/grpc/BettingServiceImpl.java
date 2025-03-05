package org.example.grpc;

import io.grpc.stub.StreamObserver;
import org.example.Bet;
import org.example.grpc.BettingProto.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BettingServiceImpl extends BettingServiceGrpc.BettingServiceImplBase {

    // Concurrent map to store the bets using a unique key combining bookmaker name and event
    private final ConcurrentMap<String, Bet> bets = new ConcurrentHashMap<>();

    // Method to handle offering a new bet
    @Override
    public void offerBet(BetRequest request, StreamObserver<BetResponse> responseObserver) {
        // Create a unique key for the bet using bookmaker name and event
        String betKey = request.getName() + "-" + request.getEvent();

        // Create a new Bet object from the request data
        Bet bet = new Bet(request.getName(), request.getEvent(), request.getOddsA(), request.getOddsDraw(), request.getOddsB(), request.getMaxSum());

        // Store the bet in the map
        bets.put(betKey, bet);

        // Create a response message indicating the bet was offered successfully
        BetResponse response = BetResponse.newBuilder()
                .setMessage("Bet offered successfully by " + request.getName())
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Method to handle placing a bet
    @Override
    public void placeBet(PlaceBetRequest request, StreamObserver<PlaceBetResponse> responseObserver) {
        // Logic to place a bet can be added here
        // For now, just sending a response message indicating the bet was placed successfully
        String responseMessage = "Bet placed successfully by " + request.getBettorName();
        PlaceBetResponse response = PlaceBetResponse.newBuilder().setMessage(responseMessage).build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Method to handle checking the outcome of a bet
    @Override
    public void checkOutcome(CheckOutcomeRequest request, StreamObserver<CheckOutcomeResponse> responseObserver) {
        // Logic to check the outcome of a bet can be added here
        // For now, just sending a response message indicating the outcome was checked
        String responseMessage = "Outcome checked for event " + request.getEvent();
        CheckOutcomeResponse response = CheckOutcomeResponse.newBuilder().setMessage(responseMessage).build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Method to handle listing all bets
    @Override
    public void listBets(Empty request, StreamObserver<ListBetsResponse> responseObserver) {
        // Create a response builder to build the list of bets
        ListBetsResponse.Builder responseBuilder = ListBetsResponse.newBuilder();

        // Iterate through all bets in the map and add them to the response
        for (Bet bet : bets.values()) {
            BetRequest betRequest = BetRequest.newBuilder()
                    .setName(bet.getName())
                    .setEvent(bet.getEvent())
                    .setOddsA(bet.getOddsA())
                    .setOddsDraw(bet.getOddsDraw())
                    .setOddsB(bet.getOddsB())
                    .setMaxSum(bet.getMaxSum())
                    .build();
            responseBuilder.addBets(betRequest);
        }

        // Send the response back to the client
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // Method to handle removing a bet
    @Override
    public void removeBet(RemoveBetRequest request, StreamObserver<BetResponse> responseObserver) {
        // Remove the bet from the map using the event as the key
        Bet removedBet = bets.remove(request.getEvent());

        // Create a response message indicating if the bet was removed or not found
        String message = (removedBet != null) ? "Bet removed successfully" : "No bet found for event";
        BetResponse response = BetResponse.newBuilder().setMessage(message).build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
