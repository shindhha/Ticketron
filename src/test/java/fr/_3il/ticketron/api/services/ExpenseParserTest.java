package fr._3il.ticketron.api.services;

import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.exceptions.InvalidExpenseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
  void testFromJson_validJson() {
    String json = """
                {
                  "merchant": "Amazon",
                  "date": "12/08/2024",
                  "totalAmount": "25.50",
                  "currency": "EUR",
                  "description": "USB cable",
                  "categoryCode": "TECH"
                }
                """;

    ExpenseCandidate ec = parser.fromJson(json);

    assertEquals("Amazon", ec.merchant);
    assertEquals("12/08/2024", ec.date);
    assertEquals("25.50", ec.totalAmount);
    assertEquals("EUR", ec.currency);
    assertEquals("USB cable", ec.description);
    assertEquals("TECH", ec.categoryCode);
  }

  @Test
  void testFromJson_invalidJson() {
    String json = "{ invalid json }";
    assertThrows(IllegalArgumentException.class, () -> parser.fromJson(json));
  }

  @Test
  void testFromJson_nullOrEmpty() {
    assertThrows(IllegalArgumentException.class, () -> parser.fromJson(null));
    assertThrows(IllegalArgumentException.class, () -> parser.fromJson(""));
  }

  @Test
  void testParseDate_validFormats() {
    assertEquals(LocalDate.of(2024, 8, 12), parser.parseDate("12/08/2024"));
    assertEquals(LocalDate.of(2024, 8, 12), parser.parseDate("12-08-2024"));
    assertEquals(LocalDate.of(2024, 8, 12), parser.parseDate("2024-08-12"));
    assertEquals(LocalDate.of(2024, 8, 12), parser.parseDate("12/08/24"));
  }

  @Test
  void testParseDate_withExtraCharacters() {
    assertEquals(LocalDate.of(2024, 8, 12), parser.parseDate("Date: 12/08/2024"));
    assertEquals(LocalDate.of(2024, 1, 3), parser.parseDate("03-01-2024 "));
  }

  @Test
  void testParseDate_invalid() {
    assertNull(parser.parseDate("32/13/2024"));
    assertNull(parser.parseDate("not a date"));
    assertNull(parser.parseDate(null));
    assertNull(parser.parseDate(""));
  }

  @Test
  void testParseAmount_validFormats() {
    assertEquals(new BigDecimal("2.50"), parser.parseAmount("2.50"));
    assertEquals(new BigDecimal("2.50"), parser.parseAmount("2,50€"));
    assertEquals(new BigDecimal("2.42"), parser.parseAmount("Total 2.42 EUR"));
    assertEquals(new BigDecimal("123.45"), parser.parseAmount("Amount: 123,45 $"));
  }

  @Test
  void testParseAmount_invalid() {
    assertNull(parser.parseAmount("No amount here"));
    assertNull(parser.parseAmount(""));
    assertNull(parser.parseAmount(null));
  }

  @Test
  void testToExpense_success() {
    ExpenseCandidate ec = new ExpenseCandidate();
    ec.merchant = "Amazon";
    ec.date = "12/08/2024";
    ec.totalAmount = "25.50";
    ec.currency = "EUR";
    ec.description = "Cable";
    ec.categoryCode = "TECH";
    ec.hour = "18:00";
    ec.categoryName = "ALIM";
    ec.categoryDescription = "Alimentation en tout genre";

    Expense e = parser.toExpense(ec);

    assertEquals("Amazon", e.merchant);
    assertEquals(LocalDate.of(2024, 8, 12), e.date);
    assertEquals(new BigDecimal("25.50"), e.totalAmount);
    assertEquals("EUR", e.currency);
    assertEquals("Cable", e.description);
    assertEquals("TECH", e.categoryCode);
  }

  @Test
  void testToExpense_withEmptyMandatoryFieldThrows() {
    ExpenseCandidate ec = new ExpenseCandidate();
    ec.merchant = ""; // se comporte comme un champ vide

    assertThrows(InvalidExpenseException.class, () -> parser.toExpense(ec));
  }
}