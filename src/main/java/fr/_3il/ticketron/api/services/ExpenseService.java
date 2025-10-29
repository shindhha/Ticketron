package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.FlexibleCategory;
import fr._3il.ticketron.api.models.requests.FlexibleExpense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CategoryRepository categoryRepository;



  public ExpenseService(@Autowired ExpenseRepository er,
                        @Autowired CategoryRepository cs,
                        @Autowired ObjectMapper mapper) {
    this.expenseRepository = er;
    this.categoryRepository = cs;
  }


  public Expense saveExpense(Expense expenseToSave) {
    categoryRepository.save(expenseToSave.category);
    Expense saved = expenseRepository.save(expenseToSave);
    return saved;
  }

  @Tool("Retourne la liste complète des catégories existantes dans la base de données")
  public List<Category> getCategories() {
    return categoryRepository.findAll();
  }

  // Essaye plusieurs formats de date possibles
  private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
          DateTimeFormatter.ofPattern("dd/MM/yyyy"),
          DateTimeFormatter.ofPattern("dd-MM-yyyy"),
          DateTimeFormatter.ofPattern("yyyy-MM-dd"),
          DateTimeFormatter.ofPattern("dd/MM/yy")
  };
  @Tool("Convertit un FlexibleCategory en Category si aucune catégorie similaire n'existe")
  public Category fromFlexible(@ToolMemoryId FlexibleCategory flexibleCategory) {
    Category c = new Category();
    c.code = flexibleCategory.code != null ? flexibleCategory.code.trim().toUpperCase().substring(0,4) : null;
    c.name = flexibleCategory.name != null ? flexibleCategory.name.trim() : null;
    c.description = flexibleCategory.description != null ? flexibleCategory.description.trim() : null;
    return c;
  }

  public Expense fromFlexible(FlexibleExpense f) {
    Expense e = new Expense();
    if (f.merchant != null) {
      e.merchant = normalizeMerchant(f.merchant);
    }
    e.date = parseDate(f.date);
    e.totalAmount = parseAmount(f.totalAmount);
    e.currency = normalizeCurrency(f.currency);
    e.description = cleanText(f.description);

    return e;
  }

  private LocalDate parseDate(String raw) {
    if (raw == null || raw.isBlank()) return null;
    String normalized = raw.trim().replaceAll("[^0-9/\\-]", "");
    for (DateTimeFormatter fmt : DATE_FORMATS) {
      try {
        return LocalDate.parse(normalized, fmt);
      } catch (DateTimeParseException ignored) {}
    }
    return null;
  }

  private BigDecimal parseAmount(String raw) {
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

  private String normalizeCurrency(String c) {
    if (c == null) return "EUR";
    String s = c.trim().toUpperCase(Locale.ROOT);
    if (s.contains("€") || s.contains("EUR")) return "EUR";
    if (s.contains("USD") || s.contains("$")) return "USD";
    return "EUR"; // défaut
  }

  private String normalizeMerchant(String merchant) {
    return merchant.trim()
            .replaceAll("[^A-Za-zÀ-ÿ0-9 '&\\-]", "")
            .replaceAll("\\s+", " ")
            .toUpperCase(Locale.ROOT);
  }

  private String cleanText(String text) {
    if (text == null) return null;
    return text.trim().replaceAll("\\s+", " ");
  }

  // Méthode utilitaire pour parser les montants sans planter
  private BigDecimal safeBigDecimal(String value) {
    try {
      if (value == null || value.isBlank()) return null;
      return new BigDecimal(value.replace(",", "."));
    } catch (NumberFormatException e) {
      System.err.println("⚠️ Erreur de parsing BigDecimal : " + value);
      return null;
    }
  }
}
