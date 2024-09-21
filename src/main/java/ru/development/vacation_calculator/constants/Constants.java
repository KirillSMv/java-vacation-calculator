package ru.development.vacation_calculator.constants;

import java.time.format.DateTimeFormatter;

public interface Constants {
    DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    Integer NUMBER_OF_MONTHS = 12;
    Double AVERAGE_NUMBER_OF_DAYS_PER_MONTH = 29.3;
    Short TAXES = 13;

}
