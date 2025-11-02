package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.exceptions.InvalidExpenseException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for converting JSON to/from FlexibleExpense.
 */
@Service
public class ExpenseParser {

  private final ObjectMapper mapper;

  public ExpenseParser() {
    mapper = new ObjectMapper()
            // ignore unknown fields to avoid LLM hallucination errors
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  // Essaye plusieurs formats de date possibles
  private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
          DateTimeFormatter.ofPattern("dd/MM/yyyy"),
          DateTimeFormatter.ofPattern("dd-MM-yyyy"),
          DateTimeFormatter.ofPattern("yyyy-MM-dd"),
          DateTimeFormatter.ofPattern("dd/MM/yy")
  };

  /**
   * Converts a JSON string to a FlexibleExpense object.
   *
   * @param json JSON representation of a FlexibleExpense
   * @return FlexibleExpense instance
   */
  public ExpenseCandidate fromJson(String json) {
    if (json == null || json.isBlank()) {
      throw new IllegalArgumentException("Cannot parse FlexibleExpense: JSON input is null or empty");
    }

    try {
      return mapper.readValue(json, ExpenseCandidate.class);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON for FlexibleExpense:\n" + json, e);
    }
  }

  public Expense toExpense(String s) {
    ExpenseCandidate ec = fromJson(s);
    return toExpense(ec);
  }

  public Expense toExpense(ExpenseCandidate ec) {
    String empty = ec.getEmptyField();
    if (!empty.isBlank())
      throw new InvalidExpenseException("Field : " + empty + " MUST NOT BE EMPTY ");
    Expense e = new Expense();
    if (ec.merchant != null) {
      e.merchant = ec.merchant;
    }
    e.date = parseDate(ec.date);
    e.totalAmount = parseAmount(ec.totalAmount);
    e.currency = ec.currency;
    e.description = ec.description;
    e.categoryCode = ec.categoryCode;
    return e;
  }

  public LocalDate parseDate(String raw) {
    if (raw == null || raw.isBlank()) return null;
    String normalized = raw.trim().replaceAll("[^0-9/\\-]", "");
    for (DateTimeFormatter fmt : DATE_FORMATS) {
      try {
        return LocalDate.parse(normalized, fmt);
      } catch (DateTimeParseException ignored) {}
    }
    return null;
  }

  public BigDecimal parseAmount(String raw) {
    if (raw == null || raw.isBlank()) return null;
    // Exemple : "2.50", "2,50€", "Total 2.42 EUR"
    Matcher m = Pattern.compile("(\\d+[.,]?\\d*)").matcher(raw);
    if (m.find()) {
      String num = m.group(1).replace(",", ".");
      try {
        return new BigDecimal(num);
      } catch (NumberFormatException ignored) {}
    }
    return null;
  }







}
