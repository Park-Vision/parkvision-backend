package net.parkvision.parkvisionbackend.exception;

public class ReservationConflictException extends Exception {
    public ReservationConflictException(String message) {
        System.out.println(message);
    }
}
