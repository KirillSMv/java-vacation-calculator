package ru.development.vacation_calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.development.vacation_calculator.model.VacationData;
import ru.development.vacation_calculator.service.VacationService;
import ru.development.vacation_calculator.validation.VacationDataValidator;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@RestController
@RequestMapping("/calculate")
@Slf4j
@RequiredArgsConstructor
@Validated
public class VacationPaymentController {
    private final VacationService vacationService;
    private final VacationDataValidator vacationDataValidator;

    @GetMapping
    public ResponseEntity<Double> calculate(@RequestParam(value = "salary")
                                            @Positive(message = "Неверно указана зарплата сотрудника") double salary,
                                            @RequestParam(value = "days", required = false)
                                            @Min(value = (1), message = "Неверно указано количество дней отпуска") Integer days,
                                            @RequestParam(value = "start", required = false) LocalDate start,
                                            @RequestParam(value = "end", required = false) LocalDate end) {
        log.debug("'calculate' is called with salary = {}, days = {}, start = {}, end = {}", salary, days, start, end);
        VacationData vacationData = new VacationData(salary, days, start, end);
        vacationDataValidator.validate(vacationData);
        return new ResponseEntity<>(vacationService.calculateVacationPay(vacationData), HttpStatus.OK);
    }
}
