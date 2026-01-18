package com.thom.elevatorcontroller.model;

import java.util.Objects;

/**
 * A request made to the elevator system.
 *
 * <p>Per {@code PLAN.md}, a call is one of:
 * <ul>
 *   <li>a hall up call ({@code hallUp == true})</li>
 *   <li>a hall down call ({@code hallDown == true})</li>
 *   <li>a car call ({@code car == true})</li>
 * </ul>
 *
 * <p>Exactly one of {@code hallUp}, {@code hallDown}, {@code car} must be true.
 */
public record Call(boolean hallUp, boolean hallDown, boolean car, int floor) {

    public static final int MIN_FLOOR = 0;
    public static final int MAX_FLOOR = 19;

    public Call {
        validateFloor(floor);
        validateExactlyOneTypeSelected(hallUp, hallDown, car);
    }

    public static Call hallUp(int floor) {
        return new Call(true, false, false, floor);
    }

    public static Call hallDown(int floor) {
        return new Call(false, true, false, floor);
    }

    public static Call car(int floor) {
        return new Call(false, false, true, floor);
    }

    public boolean isHallUp() {
        return hallUp;
    }

    public boolean isHallDown() {
        return hallDown;
    }

    public boolean isCar() {
        return car;
    }

    private static void validateFloor(int floor) {
        if (floor < MIN_FLOOR || floor > MAX_FLOOR) {
            throw new IllegalArgumentException(
                    "floor must be in range [" + MIN_FLOOR + ", " + MAX_FLOOR + "]: " + floor);
        }
    }

    private static void validateExactlyOneTypeSelected(boolean hallUp, boolean hallDown, boolean car) {
        int count = 0;
        if (hallUp) {
            count++;
        }
        if (hallDown) {
            count++;
        }
        if (car) {
            count++;
        }

        if (count != 1) {
            throw new IllegalArgumentException(
                    "Exactly one of hallUp, hallDown, or car must be true (got hallUp="
                            + hallUp
                            + ", hallDown="
                            + hallDown
                            + ", car="
                            + car
                            + ")");
        }
    }
}
