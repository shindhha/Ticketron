package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class ExpenseServiceTest {
  @Mock
  private ExpenseRepository expenseRepository;
  @Mock
  private CategoryRepository categoryRepository;
  @Spy
  private ObjectMapper objectMapper;

  @InjectMocks
  private ExpenseService expenseTools;

  @BeforeEach
  void setUp() {
    this.objectMapper = new ObjectMapper();

  }

  // ======================================================
  // ✅ GIVEN une catégorie inexistante
  // ======================================================
  @Test
  void shouldCreateExpenseAndNewCategory_WhenCategoryDoesNotExist() {
    // GIVEN
    String json = """
        {
          "merchant": "Carrefour",
          "date": "2025-10-22",
          "totalAmount": 45.60,
          "vatAmount": 7.60,
          "currency": "EUR",
          "category": { "name": "Courses" },
          "description": "Courses alimentaires",
          "imagePath": "/uploads/receipt1.jpg"
        }
        """;

    Category newCategory = new Category();
    newCategory.name = "Courses";


    when(categoryRepository.findByNameIgnoreCase("Courses")).thenReturn(Optional.empty());
    when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

    Expense savedExpense = Expense.builder().id(10L).merchant("Carrefour").build();
    when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

    // WHEN
    String result = expenseTools.saveExpenseFromJson(json);

    // THEN
    assertEquals("Expense saved with ID 10", result);
    verify(categoryRepository).save(any(Category.class));
    verify(expenseRepository).save(any(Expense.class));
  }

  // ======================================================
  // ✅ GIVEN une catégorie déjà existante
  // ======================================================
  @Test
  void shouldReuseExistingCategory_WhenCategoryExists() {
    // GIVEN
    String json = """
        {
          "merchant": "Uber",
          "date": "2025-10-23",
          "totalAmount": 23.50,
          "vatAmount": 2.35,
          "currency": "EUR",
          "category": { "name": "Transport" },
          "description": "Course en taxi",
          "imagePath": "/uploads/receipt2.jpg"
        }
        """;

    Category existingCategory = new Category();
    existingCategory.name = "Transport";
    when(categoryRepository.findByNameIgnoreCase("Transport"))
            .thenReturn(Optional.of(existingCategory));

    Expense savedExpense = Expense.builder().id(20L).merchant("Uber").build();
    when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

    // WHEN
    String result = expenseTools.saveExpenseFromJson(json);

    // THEN
    assertEquals("Expense saved with ID 20", result);
    verify(categoryRepository, never()).save(any(Category.class)); // pas de création
    verify(expenseRepository).save(any(Expense.class));

    // Vérifie que la catégorie associée est la bonne
    ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
    verify(expenseRepository).save(captor.capture());
    assertEquals("Transport", captor.getValue().category.name);
  }

  // ======================================================
  // ❌ GIVEN un JSON invalide
  // ======================================================
  @Test
  void shouldReturnError_WhenJsonInvalid() {
    // GIVEN
    String invalidJson = "{ invalid json }";

    // WHEN
    String result = expenseTools.saveExpenseFromJson(invalidJson);

    // THEN
    assertTrue(result.startsWith("Error while saving expense:"));
    verifyNoInteractions(categoryRepository, expenseRepository);
  }

  // ======================================================
  // ✅ GIVEN un JSON sans catégorie
  // ======================================================
  @Test
  void shouldSaveExpenseWithoutCategory_WhenCategoryIsNull() {
    // GIVEN
    String json = """
        {
          "merchant": "Fnac",
          "date": "2025-10-20",
          "totalAmount": 120.00,
          "vatAmount": 20.00,
          "currency": "EUR",
          "description": "Achat matériel pro",
          "imagePath": "/uploads/receipt3.jpg"
        }
        """;

    Expense savedExpense = Expense.builder().id(30L).merchant("Fnac").build();
    when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

    // WHEN
    String result = expenseTools.saveExpenseFromJson(json);

    // THEN
    assertEquals("Expense saved with ID 30", result);
    verify(expenseRepository).save(any(Expense.class));
    verifyNoInteractions(categoryRepository);
  }
}