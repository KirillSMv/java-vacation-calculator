package ru.development.vacation_calculator.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.development.vacation_calculator.constants.Constants;
import ru.development.vacation_calculator.model.VacationData;
import ru.development.vacation_calculator.supportclasses.BankHolidays;

import java.time.LocalDate;
import java.time.Month;

@ExtendWith(MockitoExtension.class)
class VacationServiceImplTest {

    @Mock
    private BankHolidays bankHolidays;

    @InjectMocks
    private VacationServiceImpl vacationService;

    static VacationData vacationWithDaysOnly;
    static VacationData vacationDataWithDaysAndStart;
    static VacationData vacationWithAllFields;
    static VacationData vacationIncludesHoliday;
    static VacationData vacationWithStartOnlyIncludesHoliday;

    @BeforeAll
    static void setUp() {
        vacationWithDaysOnly = VacationData.builder().salary(600000.0).vacationDays(10).build();
        vacationDataWithDaysAndStart = VacationData.builder()
                .salary(600000.0)
                .vacationDays(10)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .build();
        vacationWithAllFields = VacationData.builder()
                .salary(600000.0)
                .vacationDays(10)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.SEPTEMBER, 10))
                .build();

        vacationIncludesHoliday = VacationData.builder()
                .salary(600000.0)
                .vacationDays(9)
                .vacationStart(LocalDate.of(2024, Month.NOVEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.NOVEMBER, 10))
                .build();

        vacationWithStartOnlyIncludesHoliday = VacationData.builder()
                .salary(600000.0)
                .vacationDays(9)
                .vacationStart(LocalDate.of(2024, Month.NOVEMBER, 01))
                .build();
    }

    @Test
    void calculateVacationPay_whenVacationWithDaysOnly_thenCalculateVacationPayment() {
        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        System.out.println(averageDailyIncome);
        double result = Math.floor((averageDailyIncome * vacationWithDaysOnly.getVacationDays() -
                (averageDailyIncome * vacationWithDaysOnly.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;

        double vacationPayment = vacationService.calculateVacationPay(vacationWithDaysOnly);

        Assertions.assertEquals(result, vacationPayment);
    }

    @Test
    void calculateVacationPay_whenVacationDataWithDaysAndStart_thenCalculateVacationPayment() {
        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        System.out.println(averageDailyIncome);
        double result = Math.floor((averageDailyIncome * vacationWithDaysOnly.getVacationDays() -
                (averageDailyIncome * vacationWithDaysOnly.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;

        double vacationPayment = vacationService.calculateVacationPay(vacationDataWithDaysAndStart);

        Assertions.assertEquals(result, vacationPayment);
    }

    @Test
    void calculateVacationPay_whenVacationWithAllFields_thenCalculateVacationPayment() {
        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        double result = Math.floor((averageDailyIncome * vacationWithDaysOnly.getVacationDays() -
                (averageDailyIncome * vacationWithDaysOnly.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;

        double vacationPayment = vacationService.calculateVacationPay(vacationWithAllFields);

        Assertions.assertEquals(result, vacationPayment);
    }

    @Test
    void calculateVacationPay_whenVacationIncludesHoliday_thenDurationNumberOfVacationDaysExcludesHoliday() {
        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        double result = Math.floor((averageDailyIncome * (vacationIncludesHoliday.getVacationDays()) -
                (averageDailyIncome * vacationIncludesHoliday.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;


        Mockito.when(bankHolidays.checkNumberOfHolidays(vacationIncludesHoliday.getVacationStart(),
                        vacationIncludesHoliday.getVacationEnd()))
                .thenReturn((short) 1);
        double vacationPayment = vacationService.calculateVacationPay(vacationIncludesHoliday);

        Assertions.assertEquals(result, vacationPayment);
    }

    @Test
    void calculateVacationPay_whenVacationWithStartOnlyIncludesHoliday_thenVacationExtendedForOneDayOfHoliday() {
        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        double result = Math.floor((averageDailyIncome * (vacationWithStartOnlyIncludesHoliday.getVacationDays()) -
                (averageDailyIncome * vacationWithStartOnlyIncludesHoliday.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;

        double vacationPayment = vacationService.calculateVacationPay(vacationWithStartOnlyIncludesHoliday);

        Assertions.assertEquals(result, vacationPayment);
    }
}