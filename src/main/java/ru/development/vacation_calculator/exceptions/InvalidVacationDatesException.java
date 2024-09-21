package ru.development.vacation_calculator.exceptions;

public class InvalidVacationDatesException extends RuntimeException {
    public InvalidVacationDatesException(String message) {
        super(message);
    }
}
