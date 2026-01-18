package com.thom.elevatorcontroller.model;

public record ElevatorSnapshot(int id, int currentFloor, ElevatorState state, int previousFloor, int nextFloor) {
}
