package ru.development.vacation_calculator.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.development.vacation_calculator.exceptions.InvalidVacationDatesException;
import ru.development.vacation_calculator.model.VacationData;
import ru.development.vacation_calculator.supportclasses.BankHolidays;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class VacationDataValidator {
    private final BankHolidays bankHolidays;
    private final short currentYear;

    public VacationDataValidator(BankHolidays bankHolidays, @Value("${currentYear}") short currentYear) {
        this.bankHolidays = bankHolidays;
        this.currentYear = currentYear;
    }

    public void validate(VacationData vacationData) {
        Integer vacationDays = vacationData.getVacationDays();
        LocalDate start = vacationData.getVacationStart();
        LocalDate end = vacationData.getVacationEnd();

        validateIfYearIsCurrent(start, end, vacationDays);
        checkIfStartsWithHoliday(start);
        checkIfDaysOrPeriodIsSet(vacationDays, start, end);

        if (vacationDays != null && start != null && end != null) {
            validateStartAndEndDates(start, end);
            long numberOfDaysInPeriod = ChronoUnit.DAYS.between(start, end.plusDays(1));
            short numberOfHolidaysInPeriod = bankHolidays.checkNumberOfHolidays(start, end);

            if (vacationDays > numberOfDaysInPeriod) {
                //количество дней отпуска больше, чем общее количество дней в промежутке
                log.warn("Количество дней отпуска выходит за рамки указанного периода. " +
                        "Дней отпуска {}, общее число дней {}", vacationDays, numberOfDaysInPeriod);
                throw new InvalidVacationDatesException(String.format("Количество дней отпуска выходит за рамки указанного периода. " +
                        "Дней отпуска %d, общее число дней %d", vacationDays, numberOfDaysInPeriod));
            } else if (vacationDays < numberOfDaysInPeriod && numberOfHolidaysInPeriod == 0) {
                //количество дней отпуска меньше, чем общее количество дней в указанном промежутке
                log.warn("Количество дней отпуска меньше, чем общее количество дней в указанном промежутке. " +
                        "Дней отпуска {}, общее число дней {}", vacationDays, numberOfDaysInPeriod);
                throw new InvalidVacationDatesException(String.format("Количество дней отпуска меньше, чем общее количество дней " +
                        "в указанном промежутке. Дней отпуска %d, общее число дней %d", vacationDays, numberOfDaysInPeriod));
            } else if ((vacationData.getVacationDays() + numberOfHolidaysInPeriod) != numberOfDaysInPeriod) {
                //количество дней отпуска с учетом праздников превышает общее число дней в промежутке
                log.warn("Проверьте правильность переданных данных, количество дней отпуска с учетом праздников " +
                                "отличается от общего количества дней в указанном промежутке. " +
                                "Количество дней отпуска: {}, начало отпуска - {}, окончание отпуска- {}",
                        vacationData.getVacationDays(), vacationData.getVacationStart(), vacationData.getVacationEnd());
                throw new InvalidVacationDatesException(String.format("Проверьте правильность переданных данных, " +
                                "количество дней отпуска с учетом праздников отличается от общего количества дней в указанном промежутке. " +
                                "Количество дней отпуска: %d, начало отпуска - %td-%tm-%tY, окончание отпуска- %td-%tm-%tY",
                        vacationData.getVacationDays(), vacationData.getVacationStart(), vacationData.getVacationStart(),
                        vacationData.getVacationStart(), vacationData.getVacationEnd(), vacationData.getVacationEnd(), vacationData.getVacationEnd()));
            }
        }
    }

    private void checkIfDaysOrPeriodIsSet(Integer vacationDays, LocalDate start, LocalDate end) {
        if (vacationDays == null && (start == null || end == null)) { //не указана длительность и точные даты отпуска
            log.warn("Пожалуйста, укажите количество дней отпуска или дату начала и окончания отпуска");
            throw new InvalidVacationDatesException("Пожалуйста, укажите количество дней отпуска " +
                    "или дату начала и окончания отпуска");
        }
    }

    private void validateIfYearIsCurrent(LocalDate start, LocalDate end, Integer days) {
        if (start != null) {
            if (start.getYear() != currentYear) {
                log.warn("Обратите внимание, что калькулятор работает только для текущего года");
                throw new InvalidVacationDatesException("Обратите внимание, что калькулятор работает только для текущего года");
            }
            if (end != null) {
                if (end.getYear() != currentYear) {
                    log.warn("Обратите внимание, что калькулятор работает только для текущего года");
                    throw new InvalidVacationDatesException("Обратите внимание, что калькулятор работает только для текущего года");
                }
                if (start.plusDays(days).getYear() != currentYear) {
                    log.warn("Обратите внимание, что калькулятор работает только для текущего года");
                    throw new InvalidVacationDatesException("Обратите внимание, что калькулятор работает только для текущего года");
                }
            }
        }
    }


    private void checkIfStartsWithHoliday(LocalDate start) {
        if (start != null) {
            boolean isAHoliday = bankHolidays.checkIfAHoliday(start);
            if (isAHoliday) {
                log.warn("Отпуск не может начинаться с выходного дня, пожалуйста, " +
                        "измените дату начала отпуска. Текущая дата начала отпуска - {}", start);
                throw new InvalidVacationDatesException(String.format("Отпуск не может начинаться с выходного дня, пожалуйста, " +
                        "измените дату начала отпуска. Текущая дата начала отпуска - %td-%tm-%tY", start, start, start));
            }
        }
    }

    private void validateStartAndEndDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start) && end.isEqual(start)) {  //не корректно указаны точные даты отпуска
            log.warn("Пожалуйста, проверьте правильность указанных дат начала " +
                    "и окончания отпуска, начало отпуска - {}, окончание отпуска- {}", start, end);
            throw new InvalidVacationDatesException(String.format("Пожалуйста, проверьте правильность указанных дат начала " +
                            "и окончания отпуска, начало отпуска - %tY-%tm-%td, окончание отпуска- %td-%tm-%tY",
                    start, start, start, end, end, end));
        }
    }
}
