package ru.development.vacation_calculator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.development.vacation_calculator.constants.Constants;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HolidaysChecker {
    private final Set<LocalDate> holidays;

    public HolidaysChecker(@Value("#{'${listOfHolidays}'.split(',')}") Set<String> holidays) {
        this.holidays = holidays.stream().map(date -> LocalDate.parse(date, Constants.DATE_PATTERN)).collect(Collectors.toSet());
    }

    public int checkNumberOfHolidays(LocalDate start, LocalDate end) {
        long count = holidays.stream()
                .filter(element -> (element.isBefore(end) || element.isEqual(end)) && element.isAfter(start))
                .count();
        log.debug("Number of holidays in period: {}", count);
        return (int) count;
    }

    public boolean checkIfAHoliday(LocalDate date) {
        return holidays.contains(date);
    }
}
