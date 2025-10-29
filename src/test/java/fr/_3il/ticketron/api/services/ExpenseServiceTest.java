package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;

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


  @BeforeEach
  void setUp() {
    this.objectMapper = new ObjectMapper();
  }




}