package ru.development.vacation_calculator.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.development.vacation_calculator.exceptions.InvalidVacationDatesException;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import static ru.development.vacation_calculator.constants.Constants.TIME_PATTERN;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, DateTimeException.class, InvalidVacationDatesException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(RuntimeException exception) {
        log.warn("Exception message: {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage(), LocalDateTime.now().format(TIME_PATTERN));
    }
}
