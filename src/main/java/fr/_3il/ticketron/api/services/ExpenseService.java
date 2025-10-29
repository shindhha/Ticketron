package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
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

  @Tool(value = "Enregistre une depense a partir des informations fournies dans le builder.")
  public Expense saveExpense(Expense expenseToSave) {
    Expense saved = expenseRepository.save(expenseToSave);
    return saved;
  }
  @Tool(value = "Retourne la liste de toutes les categories de depenses disponibles.")
  public List<Category> getCategories() {
    return categoryRepository.findAll();
  }
  @Tool(value = "Ajoute une nouvelle categorie de depense avec un nom et une description.")
  public Category addCategory(
          @P("Le code de la catégorie, 4 lettres majuscules.")
          String code,
          String name, String description) {
    Category category = new Category();
    category.code = code.trim().toUpperCase();
    category.name = name;
    category.description = description;
    Category saved = categoryRepository.save(category);
    return saved;

  }
  @Tool(value = "Construit une depense Expense à partir d'une depense flexible FlexibleExpense.")
  public Expense buildExpense(FlexibleExpense flexibleExpense) {
    // --- Date parsing ---
    LocalDate date = null;
    LocalDateTime dateTime = null;

    // Essayons de parser la date
    try {
      if (flexibleExpense.hour != null && !flexibleExpense.hour.isBlank()) {
        // Combine date + heure
        String dateTimeStr = flexibleExpense.date + " " + flexibleExpense.hour;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        dateTime = LocalDateTime.parse(dateTimeStr, dtf);
        date = dateTime.toLocalDate();
      } else {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date = LocalDate.parse(flexibleExpense.date, df);
      }
    } catch (DateTimeParseException e) {
      System.err.println("⚠️ Erreur de parsing de la date : " + flexibleExpense.date + " " + flexibleExpense.hour);
      e.printStackTrace();
    }

    // --- Conversion sécurisée des montants ---
    BigDecimal totalAmount = safeBigDecimal(flexibleExpense.totalAmount);

    // --- Construction de l'objet ---
    Expense expense = new Expense();


    return expense;
  }

  // Méthode utilitaire pour parser les montants sans planter
  private static BigDecimal safeBigDecimal(String value) {
    try {
      if (value == null || value.isBlank()) return null;
      return new BigDecimal(value.replace(",", "."));
    } catch (NumberFormatException e) {
      System.err.println("⚠️ Erreur de parsing BigDecimal : " + value);
      return null;
    }
  }
}
