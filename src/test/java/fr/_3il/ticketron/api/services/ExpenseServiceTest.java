package fr._3il.ticketron.api.services;

import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.FlexibleExpense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ExpenseServiceTest {
  @MockitoBean
  private ExpenseRepository expenseRepository;
  @MockitoBean
  private CategoryRepository categoryRepository;

  @Autowired
  private ExpenseService expenseTools;



  @Test
  void buildExpense() {
    FlexibleExpense fe = new FlexibleExpense();
    fe.merchant = "MARKET Hauteville";
    fe.totalAmount = "2,50 €";
    fe.date = "2024-10-15";
    fe.currency = "EUR";
    fe.description = "Achat de fruits";
    fe.hour = "14:30";
    Expense expense = expenseTools.fromFlexible(fe);
    assertEquals(new BigDecimal("2.50"), expense.totalAmount);
    assertEquals("2024-10-15", expense.date.toString());
  }



}