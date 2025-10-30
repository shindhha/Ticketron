package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.repositories.CategoryRepository;
import fr._3il.ticketron.api.repositories.ExpenseReportRepository;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseReportRepository expenseReportRepository;
  private final CategoryRepository categoryRepository;

  private final Expense.ExpenseBuilder expenseBuilder;

  public ExpenseService(@Autowired ExpenseRepository er,
                        @Autowired ExpenseReportRepository ers,
                        @Autowired CategoryRepository cs,
                        @Autowired ObjectMapper mapper,
                        @Autowired Expense.ExpenseBuilder eb) {
    this.expenseRepository = er;
    this.expenseReportRepository = ers;
    this.categoryRepository = cs;
    this.expenseBuilder = eb;
  }

  @Tool(value = "Enregistre une depense a partir des informations fournies dans le builder.")
  public Expense saveExpense() {
    Expense expense = expenseBuilder.build();
    Expense saved = expenseRepository.save(expense);
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
}
