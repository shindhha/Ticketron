package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseReportRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseReportRepository expenseReportRepository;
  private final CategoryRepository categoryRepository;
  private final ObjectMapper mapper;

  public ExpenseService(@Autowired ExpenseRepository er,
                        @Autowired ExpenseReportRepository ers,
                        @Autowired CategoryRepository cs,
                        @Autowired ObjectMapper mapper) {
    this.expenseRepository = er;
    this.expenseReportRepository = ers;
    this.categoryRepository = cs;
    this.mapper = mapper;
  }

  @Tool(name = "saveExpenseFromJson",
          value = "Crée une dépense à partir d'un objet JSON complet contenant les champs d'une note de frais.")
  public String saveExpenseFromJson(String json) {
    try {
      Expense expense = mapper.readValue(json, Expense.class);

      // Gestion de la catégorie
      if (expense.category != null && expense.category.name != null) {
        String catName = expense.category.name.trim();
        Category category = categoryRepository.findByNameIgnoreCase(catName)
                .orElseGet(() -> {
                  Category category1 = new Category();
                  category1.name = catName;
                  category1.description = "";
                  return categoryRepository.save(category1);
                });
        expense.category = category;
      }

      Expense saved = expenseRepository.save(expense);
      return "Expense saved with ID " + saved.getId();

    } catch (Exception e) {
      e.printStackTrace();
      return "Error while saving expense: " + e.getMessage();
    }
  }
}
