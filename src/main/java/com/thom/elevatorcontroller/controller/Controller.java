package com.thom.elevatorcontroller.controller;

import com.thom.elevatorcontroller.model.Call;
import com.thom.elevatorcontroller.worker.Elevator;
import com.thom.elevatorcontroller.model.ElevatorSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

/**
 * Central orchestrator.
 *
 * <p>
 * Skeleton only: should run as a single thread, taking calls from the inbound
 * queue and assigning them to an elevator.
 */
public final class Controller implements Runnable {

    private final BlockingQueue<Call> inboundQueue;
    private final List<Elevator> elevators;

    public Controller(BlockingQueue<Call> inboundQueue, List<Elevator> elevators) {
        this.inboundQueue = Objects.requireNonNull(inboundQueue, "inboundQueue");
        this.elevators = List.copyOf(elevators);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Call call = inboundQueue.take();
                if (call.isCar())
                    continue; // INFO: not supported in v1
                Elevator elevator = selectBestElevator(call);
                elevator.assignFloor(call.floor());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Elevator selectBestElevator(Call call) {
        int[] elevatorEffort = new int[elevators.size()];
        for (int i = 0; i < elevators.size(); i++) {
            ElevatorSnapshot snapshot = elevators.get(i).snapshot();
            elevatorEffort[i] = calculateElevatorEffort(snapshot, call);
        }

        int bestEffort = elevatorEffort[0];
        int bestElevator = 0;
        for (int i = 1; i < elevators.size(); i++) {
            if (elevatorEffort[i] < bestEffort) {
                bestEffort = elevatorEffort[i];
                bestElevator = i;
            }
        }
        return elevators.get(bestElevator);
    }

    private int calculateElevatorEffort(ElevatorSnapshot snapshot, Call call) {
        final int HIGH_PENALTY = 20 * 2;
        final int LOW_PENALTY = 20 * 1;

        int distance = Math.abs(snapshot.currentFloor() - call.floor());
        switch (snapshot.state()) {
            case IDLE:
                return distance; // optimal
            case MOVING_UP:
                if (call.isHallUp()) {
                    if (call.floor() > snapshot.currentFloor()) {
                        return distance;
                    }
                    return distance + HIGH_PENALTY;
                } else if (call.isHallDown()) {
                    // INFO: this is explicit, as it helps me reason about the logic for the minute
                    return distance + LOW_PENALTY;
                }
            case MOVING_DOWN:
                if (call.isHallDown()) {
                    if (call.floor() < snapshot.currentFloor()) {
                        return distance;
                    }
                    return distance + HIGH_PENALTY;
                } else if (call.isHallUp()) {
                    // INFO: this is explicit, as it helps me reason about the logic for the minute
                    return distance + LOW_PENALTY;
                }
        }
        // Bad practice ik
        return 0;
    }
}
