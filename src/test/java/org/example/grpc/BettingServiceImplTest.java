package org.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.grpc.BettingProto.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BettingServiceImplTest {
    private static ManagedChannel channel;
    private static BettingServiceGrpc.BettingServiceBlockingStub blockingStub;
    private static BettingServiceGrpc.BettingServiceStub asyncStub;

    @BeforeAll
    public static void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        blockingStub = BettingServiceGrpc.newBlockingStub(channel);
        asyncStub = BettingServiceGrpc.newStub(channel);
    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void testOfferBet() {
        BetRequest request = BetRequest.newBuilder()
                .setName("Bookmaker1")
                .setEvent("TeamA-TeamB")
                .setOddsA("2.0")
                .setOddsDraw("3.0")
                .setOddsB("4.0")
                .setMaxSum(1000)
                .build();

        BetResponse response = blockingStub.offerBet(request);
        assertEquals("Bet offered successfully by Bookmaker1", response.getMessage());
    }

    // Add more tests for placeBet, checkOutcome, listBets, and removeBet
}
