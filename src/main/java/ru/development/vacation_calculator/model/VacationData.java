package ru.development.vacation_calculator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class VacationData {
    private Double salary;

    private Integer vacationDays;

    private LocalDate vacationStart;

    private LocalDate vacationEnd;
}
