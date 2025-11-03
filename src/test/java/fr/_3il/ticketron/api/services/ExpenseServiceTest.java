package fr._3il.ticketron.api.services;

import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExpenseServiceTest {
  @MockitoBean
  private ExpenseRepository expenseRepo;
  @MockitoBean
  private CategoryService categoryService;

  @Autowired
  private ExpenseService service;
  @MockitoSpyBean
  private ExpenseParser parser;



    @Test
    void testSaveExpense_success() {
      // given JSON input
      String json = "<CODE>MARKET</CODE><SUM>Food</SUM> On <DATE>01/02/2016</DATE> at <TIME>18:25:08</TIME>, I bought PALMITO LU for 1.00 and PET 1L COCA COLA L for 1.42 at MARKET Hauteville, total amount was 2.50 with a change of -0.08.";

      // mocked ExpenseCandidate returned by parser
      ExpenseCandidate ec = new ExpenseCandidate();
      ec.categoryCode = "TECH";
      ec.categoryName = "Technology";
      ec.categoryDescription = "Tech products";

      // mocked Expense returned by parser.toExpense()
      Expense expense = new Expense();
      expense.categoryCode = "TECH";


      when(parser.parseExpense(json)).thenReturn(ec);

      Expense savedExpense = new Expense();
      when(expenseRepo.save(expense)).thenReturn(expense);

      // when
      String result = service.saveExpense(json);

      // then
      verify(parser).parseExpense(json);
      // Expense must be persisted
      verify(expenseRepo).save(expense);
    }



}