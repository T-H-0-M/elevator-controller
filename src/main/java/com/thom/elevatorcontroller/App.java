package com.thom.elevatorcontroller;

import com.thom.elevatorcontroller.controller.Controller;
import com.thom.elevatorcontroller.model.Call;
import com.thom.elevatorcontroller.worker.Elevator;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Application entry point.
 *
 * <p>
 * Currently a scaffold that wires up core components.
 */
public class App {
    public static void main(String[] args) {
        BlockingQueue<Call> inboundCalls = new LinkedBlockingQueue<>();

        List<Elevator> elevators = List.of(
                new Elevator(0, 0),
                new Elevator(1, 0),
                new Elevator(2, 0),
                new Elevator(3, 0));

        Controller controller = new Controller(inboundCalls, elevators);

        System.out.println("Elevator controller scaffold ready.");

        ExecutorService executor = Executors.newFixedThreadPool(elevators.size() + 1);
        try {
            for (Elevator elevator : elevators) {
                executor.submit(elevator);
            }
            executor.submit(controller);

            // INFO: dummy calls for now
            inboundCalls.put(Call.hallUp(3));
            inboundCalls.put(Call.hallDown(10));
            inboundCalls.put(Call.hallUp(15));
            inboundCalls.put(Call.hallDown(7));

            Thread.sleep(Duration.ofSeconds(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
