package com.thom.elevatorcontroller.worker;

import com.thom.elevatorcontroller.model.ElevatorSnapshot;
import com.thom.elevatorcontroller.model.ElevatorState;

import java.util.BitSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Elevator worker.
 *
 * <p>
 * Skeleton only: the elevator thread should be the sole mutator of
 * {@code floorsToVisit}. External threads should enqueue assignments via
 * {@link #assignFloor(int)}.
 */
public final class Elevator implements Runnable {

    private final int id;
    private final BitSet floorsToVisit = new BitSet();
    private final BlockingQueue<Integer> assignedStops;

    private volatile int currentFloor;
    private volatile int previousFloor;
    private volatile int nextFloor;
    private volatile ElevatorState state = ElevatorState.IDLE;

    public Elevator(int id, int startingFloor) {
        this(id, startingFloor, new LinkedBlockingQueue<>());
    }

    public Elevator(int id, int startingFloor, BlockingQueue<Integer> assignedStops) {
        this.id = id;
        this.currentFloor = startingFloor;
        this.assignedStops = assignedStops;
        this.nextFloor = this.currentFloor;
        this.previousFloor = this.currentFloor;
    }

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorState getState() {
        return state;
    }

    public void assignFloor(int floor) {
        assignedStops.add(floor);
    }

    public synchronized ElevatorSnapshot snapshot() {
        return new ElevatorSnapshot(id, currentFloor, state, previousFloor, nextFloor);
    }

    @Override
    public void run() {
        // TODO: This is an absolutely atrocious string of logic, but its whats in my
        // brain atm
        state = ElevatorState.IDLE;
        previousFloor = currentFloor;
        nextFloor = currentFloor;
        for (;;) {
            while (!assignedStops.isEmpty()) {
                try {
                    floorsToVisit.set(assignedStops.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (floorsToVisit.isEmpty()) {
                state = ElevatorState.IDLE;
                // TODO: sleep thread until message to queue?
                // try {
                // floorsToVisit.set(assignedStops.take());
                // } catch (InterruptedException e) {
                // Thread.currentThread().interrupt();
                // }
                continue;
            } else if (state == ElevatorState.IDLE) {
                int forwardDistance = Math.abs(currentFloor - floorsToVisit.nextSetBit(0));
                int backwardDistance = Math.abs(currentFloor - floorsToVisit.previousSetBit(0));
                if (forwardDistance < backwardDistance) {
                    state = ElevatorState.MOVING_UP;
                } else {
                    state = ElevatorState.MOVING_DOWN;
                }
            } else if (state == ElevatorState.MOVING_UP) {
                try {
                    this.nextFloor = floorsToVisit.nextSetBit(0);
                    this.previousFloor = floorsToVisit.previousSetBit(0);
                    if (currentFloor == floorsToVisit.nextSetBit(0)) {
                        System.out.println("Elevator" + this.id + "stopping at floor " + currentFloor);
                        floorsToVisit.clear(currentFloor);
                        Thread.sleep(2000);
                    } else {
                        System.out.println("Elevator" + this.id + "moving past floor " + currentFloor);
                        Thread.sleep(1000);
                        ++currentFloor;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else if (state == ElevatorState.MOVING_DOWN) {
                try {
                    this.nextFloor = floorsToVisit.nextSetBit(0);
                    this.previousFloor = floorsToVisit.previousSetBit(0);
                    if (currentFloor == floorsToVisit.previousSetBit(0)) {
                        System.out.println("Elevator" + this.id + "stopping at floor " + currentFloor);
                        Thread.sleep(2000);
                        floorsToVisit.clear(currentFloor);
                    } else {
                        System.out.println("Elevator" + this.id + "moving past floor " + currentFloor);
                        Thread.sleep(1000);
                        --currentFloor;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }

        // TODO: Implement elevator loop
        // - Drain assignedStops into floorsToVisit
        // - Move toward next set bit (1s per floor)
        // - When arriving at a set bit, sleep 2s then clear bit
        // - When updating floors to visit, update next/previous floor
        // - When no work remains, become IDLE and block on assignedStops
    }
}
