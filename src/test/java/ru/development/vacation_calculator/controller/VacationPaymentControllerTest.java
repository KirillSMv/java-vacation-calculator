package ru.development.vacation_calculator.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.development.vacation_calculator.constants.Constants;
import ru.development.vacation_calculator.exceptions.InvalidVacationDatesException;
import ru.development.vacation_calculator.model.VacationData;
import ru.development.vacation_calculator.service.VacationService;
import ru.development.vacation_calculator.validation.VacationDataValidator;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VacationPaymentController.class)
class VacationPaymentControllerTest {

    @MockBean
    private VacationService vacationService;

    @MockBean
    private VacationDataValidator vacationDataValidator;

    @Autowired
    private MockMvc mockMvc;

    private static double vacationPayment;


    @BeforeAll
    static void setUp() {
        VacationData vacationWithAllFields = VacationData.builder()
                .salary(600000.0)
                .vacationDays(10)
                .vacationStart(LocalDate.of(2024, Month.SEPTEMBER, 01))
                .vacationEnd(LocalDate.of(2024, Month.SEPTEMBER, 10))
                .build();

        double averageDailyIncome = Math.floor(((double) 600000 / Constants.NUMBER_OF_MONTHS
                / Constants.AVERAGE_NUMBER_OF_DAYS_PER_MONTH) * 100) / 100;
        vacationPayment = Math.floor((averageDailyIncome * vacationWithAllFields.getVacationDays() -
                (averageDailyIncome * vacationWithAllFields.getVacationDays()) * Constants.TAXES / 100) * 100) / 100;
    }

    @Test
    void calculate() throws Exception {
        when(vacationService.calculateVacationPay(any(VacationData.class))).thenReturn(vacationPayment);
        doNothing().when(vacationDataValidator).validate(any(VacationData.class));

        mockMvc.perform(get("/calculate")
                        .param("salary", "60000")
                        .param("days", "10")
                        .param("start", "01-09-2024")
                        .param("end", "10-09-2024"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(String.valueOf(vacationPayment)));
    }


    @Test
    void calculateTest_whenValidationIsNotPassed_thenThrowInvalidVacationDatesException() throws Exception {
        when(vacationService.calculateVacationPay(any(VacationData.class))).thenReturn(vacationPayment);
        doThrow(InvalidVacationDatesException.class).when(vacationDataValidator).validate(any(VacationData.class));

        mockMvc.perform(get("/calculate")
                        .param("salary", "60000")
                        .param("end", "10-09-2024"))
                .andExpect(status().is(400))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidVacationDatesException));
    }

    @Test
    void calculateTest_whenIncorrectSalaryPassed_thenThrowNestedServletExceptionWithMessage() throws Exception {
        when(vacationService.calculateVacationPay(any(VacationData.class))).thenReturn(vacationPayment);
        doNothing().when(vacationDataValidator).validate(any(VacationData.class));

        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/calculate")
                .param("salary", "-60000")
                .param("days", "10")
                .param("start", "01-09-2024")
                .param("end", "10-09-2024")).andExpect(status().is(400)), "Неверно указана зарплата сотрудника");
    }

    @Test
    void calculateTest_whenIncorrectNumberOfDaysPassed_thenThrowNestedServletExceptionWithMessage() throws Exception {
        when(vacationService.calculateVacationPay(any(VacationData.class))).thenReturn(vacationPayment);
        doNothing().when(vacationDataValidator).validate(any(VacationData.class));

        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/calculate")
                .param("salary", "-60000")
                .param("days", "10")
                .param("start", "01-09-2024")
                .param("end", "10-09-2024")).andExpect(status().is(400)), "Неверно указано количество дней отпуска");
    }
}