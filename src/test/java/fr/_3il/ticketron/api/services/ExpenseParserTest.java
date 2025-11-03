package fr._3il.ticketron.api.services;

import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ExpenseParserTest {

  private ExpenseParser parser;

  @BeforeEach
  void setUp() {
    parser = new ExpenseParser();
  }

  @Test
  void parseExpense() {
    String description = """
              "<CODE>MARKET</CODE><SUM>Food</SUM> On <DATE>01/02/2016</DATE> at <TIME>18:25:08</TIME>, I bought PALMITO LU for 1.00 and PET 1L COCA COLA L for 1.42 at MARKET Hauteville, total amount was 2.50 with a change of -0.08."        
                """;

    ExpenseCandidate ec = parser.parseExpense(description);

    assertEquals("Food", ec.categoryDescription);
    assertEquals("MARKET", ec.categoryCode);
  }









}