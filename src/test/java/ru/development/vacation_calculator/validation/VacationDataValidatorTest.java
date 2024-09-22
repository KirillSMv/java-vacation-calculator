package ru.development.vacation_calculator.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.development.vacation_calculator.exceptions.InvalidVacationDatesException;
import ru.development.vacation_calculator.service.HolidaysChecker;
import ru.development.vacation_calculator.model.VacationData;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationDataValidatorTest {
    private static HolidaysChecker holidaysChecker;
    private static VacationDataValidator vacationDataValidator;

    private static VacationData validObject;
    private static VacationData vacationDataStartsAtHolidays;
    private static VacationData vacationDataWithoutDaysAndStart;
    private static VacationData vacationWithVacationDaysMoreThanDaysInPeriod;
    private static VacationData vacationWithVacationDaysFewerThanDaysInPeriod;
    private static VacationData vacationWithVacationDaysFewerThanDaysInPeriodWithBankHolidays;
    private static VacationData vacationWithVacationDaysMoreThanDaysInPeriodWithBankHolidays;

    @BeforeAll
    static void setUp() {
        holidaysChecker = Mockito.mock(HolidaysChecker.class);
        vacationDataValidator = new VacationDataValidator(holidaysChecker, (short) 2024);

        validObject = VacationData.builder().salary(600000.0)
                .vacationDays(10)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.SEPTEMBER, 10))
                .build();

        vacationDataStartsAtHolidays = VacationData.builder().salary(600000.0)
                .vacationDays(6)
                .vacationStart(LocalDate.of(2024, Month.NOVEMBER, 04))
                .vacationEnd(LocalDate.of(2024, Month.NOVEMBER, 10))
                .build();

        vacationDataWithoutDaysAndStart = VacationData.builder()
                .salary(600000.0)
                .build();

        vacationWithVacationDaysMoreThanDaysInPeriod = VacationData.builder()
                .salary(600000.0)
                .vacationDays(11)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.SEPTEMBER, 10))
                .build();

        vacationWithVacationDaysFewerThanDaysInPeriod = VacationData.builder()
                .salary(600000.0)
                .vacationDays(9)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.SEPTEMBER, 10))
                .build();

        vacationWithVacationDaysFewerThanDaysInPeriodWithBankHolidays = VacationData.builder()
                .salary(600000.0)
                .vacationDays(8)
                .vacationStart(LocalDate.of(2024, Month.NOVEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.NOVEMBER, 10))
                .build();

        vacationWithVacationDaysMoreThanDaysInPeriodWithBankHolidays = VacationData.builder()
                .salary(600000.0)
                .vacationDays(10)
                .vacationStart(LocalDate.of(2024, Month.NOVEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.NOVEMBER, 10))
                .build();
    }

    @Test
    void validateTest_whenValidObjectPassed_thenNoExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        vacationDataValidator.validate(validObject);
    }

    @Test
    void validateTest_whenVacationStartsAtHoliday_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationDataStartsAtHolidays));
    }


    @Test
    void validateTest_whenDaysAndStartNull_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationDataWithoutDaysAndStart));
    }

    @Test
    void validateTest_whenVacationDaysMoreThanDaysInDefinedPeriod_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationWithVacationDaysMoreThanDaysInPeriod));
    }

    @Test
    void validateTest_whenVacationDaysFewerThanDaysInDefinedPeriod_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(0);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationWithVacationDaysFewerThanDaysInPeriod));
    }

    @Test
    void validateTest_whenVacationDaysFewerThanDaysInDefinedPeriodWithBanksHolidays_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(1);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationWithVacationDaysFewerThanDaysInPeriodWithBankHolidays));
    }

    @Test
    void validateTest_whenVacationDaysMoreThanDaysInDefinedPeriodWithBanksHolidays_thenInvalidVacationDatesExceptionExceptionIsThrown() {
        when(holidaysChecker.checkNumberOfHolidays(any(LocalDate.class), any(LocalDate.class))).thenReturn(1);
        when(holidaysChecker.checkIfAHoliday(any(LocalDate.class))).thenReturn(false);

        assertThrows(InvalidVacationDatesException.class, () -> vacationDataValidator.validate(vacationWithVacationDaysMoreThanDaysInPeriodWithBankHolidays));
    }
}