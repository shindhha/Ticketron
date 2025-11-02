package fr._3il.ticketron.api.services;

import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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


<<<<<<< HEAD
  @BeforeEach
  @Disabled("Test désactivé temporairement")
  void setUp() {
    this.objectMapper = new ObjectMapper();
    expenseBuilder.reset();
  }

  // ======================================================
  // ✅ GIVEN une catégorie inexistante
  // ======================================================
  @Test
  @Disabled("Test désactivé temporairement")
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
=======

>>>>>>> LinkLLMBD


    @Test
    void testSaveExpense_success() {
      // given JSON input
      String json = "{ \"merchant\": \"Amazon\", \"date\": \"12/08/2024\", \"totalAmount\": \"25.50\", \"currency\": \"EUR\", \"categoryCode\": \"TECH\" }";

      // mocked ExpenseCandidate returned by parser
      ExpenseCandidate ec = new ExpenseCandidate();
      ec.merchant = "Amazon";
      ec.date = "12/08/2024";
      ec.totalAmount = "25.50";
      ec.currency = "EUR";
      ec.categoryCode = "TECH";
      ec.categoryName = "Technology";
      ec.categoryDescription = "Tech products";
      ec.description = "Description";
      ec.hour = "18:00";

      // mocked Expense returned by parser.toExpense()
      Expense expense = new Expense();
      expense.merchant = "Amazon";
      expense.date = LocalDate.of(2024, 8, 12);
      expense.totalAmount = new BigDecimal("25.50");
      expense.currency = "EUR";
      expense.categoryCode = "TECH";


      when(parser.fromJson(json)).thenReturn(ec);
      when(parser.toExpense(ec)).thenReturn(expense);

      Expense savedExpense = new Expense();
      when(expenseRepo.save(expense)).thenReturn(expense);

      // when
      Expense result = service.saveExpense(json);

      // then
      assertEquals("Amazon", result.merchant);
      verify(parser).fromJson(json);
      verify(parser, atMost(2)).toExpense(ec);
      // Expense must be persisted
      verify(expenseRepo).save(expense);
    }

    @Test
    void testSaveExpense_invalidJsonThrows() {

      assertThrows(IllegalArgumentException.class, () -> service.saveExpense("bad-json"));
      verify(expenseRepo, never()).save(any());
    }


}