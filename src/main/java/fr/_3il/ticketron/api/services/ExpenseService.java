package fr._3il.ticketron.api.services;

import dev.langchain4j.agent.tool.Tool;
import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service de gestion des dépenses et des catégories.
 * Fournit des outils exposés à l'agent LLM pour créer, enregistrer et consulter
 * les dépenses et leurs catégories. Agit comme intermédiaire entre l'agent IA
 * et la couche de persistance (repositories).
 */
@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CategoryService categoryService;

  private final ExpenseParser expenseCandidateParser;



  public ExpenseService(@Autowired ExpenseRepository er,
                        @Autowired CategoryService cs,
                        @Autowired ExpenseParser ecp) {
    this.expenseRepository = er;
    this.categoryService = cs;
    this.expenseCandidateParser = ecp;
  }

  @Tool("""
    Save the expense in database.
    
    Usage:
    - Provide a description of the expense with all pertinents information
    - At the end of your description categorise the expense BY adding a category code between <CODE><CODE/> and the description of the 
    category between <SUM><SUM/>
""")
  /**
   * Parse une entrée string pour retrouver un object expense et l'enregistrer dans la bd
   * @return la dépense enregistrer
   */
  public String saveExpense(String expenseToSave) {
    ExpenseCandidate ec = expenseCandidateParser.parseExpense(expenseToSave);
    Category category = new Category();
    category.code = ec.categoryCode;
    category.description = ec.categoryDescription;
    categoryService.saveIfNotExist(category);
    Expense expense = new Expense();
    expense.categoryCode = ec.categoryCode;
    expense.summary = ec.summary;
    Expense saved = expenseRepository.save(expense);
    return expenseToSave;
  }

  public List<Expense> expenses() {
    return expenseRepository.findAll();
  }






}
