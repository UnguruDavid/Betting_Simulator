package org.example.grpc;

import io.grpc.stub.StreamObserver;
import org.example.Bet;
import org.example.grpc.BettingProto.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BettingServiceImpl extends BettingServiceGrpc.BettingServiceImplBase {

    private final ConcurrentMap<String, Bet> bets = new ConcurrentHashMap<>();

    @Override
    public void offerBet(BetRequest request, StreamObserver<BetResponse> responseObserver) {
        String betKey = request.getEvent();
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
        String responseMessage = "Bet placed successfully by " + request.getBettorName();
        PlaceBetResponse response = PlaceBetResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkOutcome(CheckOutcomeRequest request, StreamObserver<CheckOutcomeResponse> responseObserver) {
        // Logic to check the outcome of a bet
        String responseMessage = "Outcome checked for event " + request.getEvent();
        CheckOutcomeResponse response = CheckOutcomeResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listBets(Empty request, StreamObserver<ListBetsResponse> responseObserver) {
        ListBetsResponse.Builder responseBuilder = ListBetsResponse.newBuilder();
        for (Bet bet : bets.values()) {
            BetRequest betRequest = BetRequest.newBuilder()
                    .setName(bet.name)
                    .setEvent(bet.event)
                    .setOddsA(bet.oddsA)
                    .setOddsDraw(bet.oddsDraw)
                    .setOddsB(bet.oddsB)
                    .setMaxSum(bet.maxSum)
                    .build();
            responseBuilder.addBets(betRequest);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeBet(RemoveBetRequest request, StreamObserver<RemoveBetResponse> responseObserver) {
        Bet removedBet = bets.remove(request.getEvent());
        String responseMessage = (removedBet != null) ? "Bet removed successfully" : "Bet not found";
        RemoveBetResponse response = RemoveBetResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
