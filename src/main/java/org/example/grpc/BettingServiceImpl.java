package org.example.grpc;

import io.grpc.stub.StreamObserver;
import org.example.grpc.BettingProto.*;

public class BettingServiceImpl extends BettingServiceGrpc.BettingServiceImplBase {

    @Override
    public void offerBet(BetRequest request, StreamObserver<BetResponse> responseObserver) {
        // Your logic to offer a bet
        String responseMessage = "Bet offered successfully by " + request.getName();
        BetResponse response = BetResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void placeBet(PlaceBetRequest request, StreamObserver<PlaceBetResponse> responseObserver) {
        // Your logic to place a bet
        String responseMessage = "Bet placed successfully by " + request.getBettorName();
        PlaceBetResponse response = PlaceBetResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkOutcome(CheckOutcomeRequest request, StreamObserver<CheckOutcomeResponse> responseObserver) {
        // Your logic to check the outcome of a bet
        String responseMessage = "Outcome checked for event " + request.getEvent();
        CheckOutcomeResponse response = CheckOutcomeResponse.newBuilder().setMessage(responseMessage).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
