package ru.development.vacation_calculator.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.development.vacation_calculator.constants.Constants;
import ru.development.vacation_calculator.model.VacationData;

import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
@Slf4j
public class VacationServiceImpl implements VacationService {
    private final HolidaysChecker holidaysChecker;

    @Override
    public double calculateVacationPay(VacationData vacationData) {
        double vacationPayment;
        //расчет без указания точных дат отпуска, либо с открытым окончанием
        if (vacationData.getVacationStart() == null || vacationData.getVacationEnd() == null) {
            vacationPayment = calculatePayment(vacationData.getSalary(), vacationData.getVacationDays());
        } else { //расчет с указанием четких дат начала и окончания отпуска
            long numberOfDaysInPeriod = ChronoUnit.DAYS.between(vacationData.getVacationStart(), vacationData.getVacationEnd().plusDays(1));
            int numberOfHolidaysInPeriod = holidaysChecker.checkNumberOfHolidays(vacationData.getVacationStart(), vacationData.getVacationEnd());
            long vacationDays = (numberOfDaysInPeriod - numberOfHolidaysInPeriod);
            vacationPayment = calculatePayment(vacationData.getSalary(), vacationDays);
        }
        return vacationPayment;
    }

    private double calculatePayment(double salaryForThePastYear, long vacationDays) {
        //Средний дневной заработок = Доходы за расчетный период / (Кол-во полных месяцев * Среднемесячное число календарных дней (29,3))
        double averageDailyIncome = Math.floor(salaryForThePastYear / (Constants.NUMBER_OF_MONTHS * Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        log.debug("averageDailyIncome = {}", averageDailyIncome);
        double finalPaymentBeforeTaxes = averageDailyIncome * vacationDays;
        double finalPaymentAfterTaxes = finalPaymentBeforeTaxes - finalPaymentBeforeTaxes * Constants.TAXES / 100;
        log.debug("finalPaymentAfterTaxes = {}", finalPaymentAfterTaxes);
        return Math.floor(finalPaymentAfterTaxes * 100) / 100;
    }
}
