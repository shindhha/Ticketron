package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.ExpenseReport;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExpenseServiceTest {
  @MockitoBean
  private ExpenseRepository expenseRepository;
  @MockitoBean
  private CategoryRepository categoryRepository;
  @MockitoSpyBean
  private ObjectMapper objectMapper;
  @Autowired
  private ExpenseService expenseTools;

  @MockitoBean
  private Expense.ExpenseBuilder expenseBuilder;

  @BeforeEach
  void setUp() {
    this.objectMapper = new ObjectMapper();
    expenseBuilder.reset();
  }

  // ======================================================
  // ✅ GIVEN une catégorie inexistante
  // ======================================================
  @Test
  void shouldBuildExpenseWithTools() {
    Expense expense = new Expense();
    expense.merchant = "MARKET Hauteville";
    expense.date = LocalDate.of(2016, 2, 1);
    expense.totalAmount = new BigDecimal("2.50");
    expense.vatAmount = BigDecimal.ZERO;
    expense.currency = "EUR";
    expense.categoryCode = "ALIM";
    expense.paymentMethod = "Espèces";
    expense.description = "Ticket de caisse du supermarché";
    expense.confidence = 1.0f;

    when(expenseBuilder.build()).thenReturn(expense);
    doReturn(null).when(expenseRepository).save(any(Expense.class));
    Expense saved = expenseTools.saveExpense();
    verify(expenseRepository).save(any(Expense.class));

  }


}