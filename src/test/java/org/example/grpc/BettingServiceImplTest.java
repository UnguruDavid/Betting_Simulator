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

    // Setup method to initialize gRPC channel and stubs
    @BeforeAll
    public static void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        blockingStub = BettingServiceGrpc.newBlockingStub(channel);
        asyncStub = BettingServiceGrpc.newStub(channel);
    }

    // Tear down method to shut down the channel after all tests are done
    @AfterAll
    public static void tearDown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // Test method for offering a bet
    @Test
    public void testOfferBet() {
        // Create a BetRequest object with test data
        BetRequest request = BetRequest.newBuilder()
                .setName("Bookmaker1")
                .setEvent("TeamA-TeamB")
                .setOddsA("2.0")
                .setOddsDraw("3.0")
                .setOddsB("4.0")
                .setMaxSum(1000)
                .build();

        // Send the request and get the response
        BetResponse response = blockingStub.offerBet(request);

        // Assert that the response message is as expected
        assertEquals("Bet offered successfully by Bookmaker1", response.getMessage());
    }

    // Add more tests for placeBet, checkOutcome, listBets, and removeBet
}
