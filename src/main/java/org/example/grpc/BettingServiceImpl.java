package org.example.grpc;

import io.grpc.stub.StreamObserver;
import org.example.Bet;  // Ensure this import statement is correct
import org.example.grpc.BettingProto.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BettingServiceImpl extends BettingServiceGrpc.BettingServiceImplBase {

    private final ConcurrentMap<String, Bet> bets = new ConcurrentHashMap<>();

    @Override
    public void offerBet(BetRequest request, StreamObserver<BetResponse> responseObserver) {
        // Add logic to handle multiple bookmakers
        String betKey = request.getName() + "-" + request.getEvent();
        Bet bet = new Bet(request.getName(), request.getEvent(), request.getOddsA(), request.getOddsDraw(), request.getOddsB(), request.getMaxSum());
        bets.put(betKey, bet);

        String responseMessage = "Bet offered successfully by " + request.getName();
        BetResponse response = BetResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void placeBet(PlaceBetRequest request, StreamObserver<PlaceBetResponse> responseObserver) {
        // Logic to place a bet
    }

    @Override
    public void checkOutcome(CheckOutcomeRequest request, StreamObserver<CheckOutcomeResponse> responseObserver) {
        // Logic to check bet outcome
    }
}
