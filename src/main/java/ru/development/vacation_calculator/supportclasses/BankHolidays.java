package ru.development.vacation_calculator.supportclasses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.development.vacation_calculator.constants.Constants;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BankHolidays {
    private final Set<LocalDate> holidays;

    public BankHolidays(@Value("#{'${listOfHolidays}'.split(',')}") Set<String> holidays) {
        this.holidays = holidays.stream().map(date -> LocalDate.parse(date, Constants.DATE_PATTERN)).collect(Collectors.toSet());
    }

    public short checkNumberOfHolidays(LocalDate start, LocalDate end) {
        List<LocalDate> vacation = new ArrayList<>();
        long numberOfDaysInPeriod = ChronoUnit.DAYS.between(start, end.plusDays(1));
        for (int i = 0; i < numberOfDaysInPeriod; i++) {
            vacation.add(start.plusDays(i));
        }
        short count = 0;
        for (LocalDate localDate : vacation) {
            if (holidays.contains(localDate)) {
                count++;
            }
        }
        log.debug("Number of holidays in period: {}", count);
        return count;
    }

    public boolean checkIfAHoliday(LocalDate date) {
        return holidays.contains(date);
    }
}
